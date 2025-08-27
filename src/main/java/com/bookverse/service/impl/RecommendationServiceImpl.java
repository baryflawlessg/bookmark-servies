package com.bookverse.service.impl;

import com.bookverse.dto.BookDTO;
import com.bookverse.dto.RecommendationDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import com.bookverse.repository.BookGenreRepository;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
import com.bookverse.service.RecommendationService;
import com.bookverse.service.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final BookRepository bookRepository;
    private final FavoriteRepository favoriteRepository;
    private final BookGenreRepository bookGenreRepository;

    @Override
    public List<RecommendationDTO> getTopRated(int limit) {
        Page<Book> page = bookRepository.findTopRatedBooks(PageRequest.of(0, limit));
        List<BookDTO> books = page.getContent().stream().map(EntityMapper::toBookDTO).collect(Collectors.toList());
        return List.of(RecommendationDTO.builder().type("top-rated").title("Top Rated Books").books(books).build());
    }

    @Override
    public List<RecommendationDTO> getGenreBased(Long userId, int limit) {
        // Simple heuristic: get most favorited genres overall as a placeholder for user's favorite genres
        var topGenres = bookGenreRepository.findTopGenres(PageRequest.of(0, 3));
        List<BookGenre.Genre> genres = topGenres.getContent().stream()
                .map(row -> (BookGenre.Genre) row[0])
                .toList();
        if (genres.isEmpty()) return Collections.emptyList();
        var booksPage = bookRepository.findByGenres(genres, PageRequest.of(0, limit));
        List<BookDTO> books = booksPage.getContent().stream().map(EntityMapper::toBookDTO).collect(Collectors.toList());
        return List.of(RecommendationDTO.builder().type("genre-based").title("Because you like these genres").books(books).build());
    }

    @Override
    public List<RecommendationDTO> getAiRecommendations(Long userId, int limit) {
        // Placeholder: return top-rated as AI recommendations for now
        return getTopRated(limit).stream()
                .map(r -> {
                    r.setType("ai");
                    r.setTitle("AI Recommendations");
                    return r;
                })
                .toList();
    }
}
