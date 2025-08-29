package com.bookverse.service.impl;

import com.bookverse.dto.BookDTO;
import com.bookverse.dto.RecommendationDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import com.bookverse.entity.Favorite;
import com.bookverse.repository.BookGenreRepository;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
import com.bookverse.service.RecommendationService;
import com.bookverse.service.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final BookRepository bookRepository;
    private final FavoriteRepository favoriteRepository;
    private final BookGenreRepository bookGenreRepository;

    @Override
    public List<RecommendationDTO> getTopRated(int limit) {
        Page<Book> page = bookRepository.findBooks(
            null, null, null, null, null, PageRequest.of(0, limit)
        );
        List<BookDTO> books = page.getContent().stream()
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
        
        return List.of(RecommendationDTO.builder()
                .type("top-rated")
                .title("Top Rated Books")
                .description("Books with the highest ratings from our community")
                .books(books)
                .build());
    }

    @Override
    public List<RecommendationDTO> getUserBasedRecommendations(Long userId, int limit) {
        List<RecommendationDTO> recommendations = new ArrayList<>();
        
        // Get user's favorite books
        List<Favorite> userFavorites = favoriteRepository.findByUserIdWithBook(userId);
        
        if (userFavorites.isEmpty()) {
            // If user has no favorites, return popular books in popular genres
            List<BookDTO> popularBooks = getPopularBooksInPopularGenres(limit);
            return List.of(RecommendationDTO.builder()
                    .type("popular")
                    .title("Popular Books")
                    .description("Trending books in our community")
                    .books(popularBooks)
                    .build());
        }
        
        // Get genres from user's favorite books
        Set<BookGenre.Genre> userFavoriteGenres = userFavorites.stream()
                .flatMap(favorite -> favorite.getBook().getGenres().stream())
                .map(BookGenre::getGenre)
                .collect(Collectors.toSet());
        
        if (!userFavoriteGenres.isEmpty()) {
            // Get books in user's favorite genres (excluding already favorited books)
            Set<Long> userFavoriteBookIds = userFavorites.stream()
                    .map(favorite -> favorite.getBook().getId())
                    .collect(Collectors.toSet());
            
            Page<Book> genreBooks = bookRepository.findBooks(
                    null, new ArrayList<>(userFavoriteGenres), null, null, null, 
                    PageRequest.of(0, limit * 2)
            );
            
            List<BookDTO> recommendedBooks = genreBooks.getContent().stream()
                    .filter(book -> !userFavoriteBookIds.contains(book.getId()))
                    .limit(limit)
                    .map(EntityMapper::toBookDTO)
                    .collect(Collectors.toList());
            
            if (!recommendedBooks.isEmpty()) {
                recommendations.add(RecommendationDTO.builder()
                        .type("user-genre-based")
                        .title("Because you like these genres")
                        .description("Books in genres you've shown interest in")
                        .books(recommendedBooks)
                        .build());
            }
        }
        
        // If we don't have enough genre-based recommendations, add popular books
        if (recommendations.isEmpty() || recommendations.get(0).getBooks().size() < limit) {
            List<BookDTO> popularBooks = getPopularBooksInPopularGenres(limit);
            if (!popularBooks.isEmpty()) {
                recommendations.add(RecommendationDTO.builder()
                        .type("popular")
                        .title("Popular Books")
                        .description("Trending books in our community")
                        .books(popularBooks)
                        .build());
            }
        }
        
        return recommendations;
    }

    @Override
    public List<RecommendationDTO> getGenreBased(int limit) {
        // Get books from popular genres (Fiction, Mystery, Romance, etc.)
        List<BookGenre.Genre> popularGenres = List.of(
            BookGenre.Genre.FICTION,
            BookGenre.Genre.MYSTERY,
            BookGenre.Genre.ROMANCE,
            BookGenre.Genre.SCI_FI
        );
        
        Page<Book> genreBooks = bookRepository.findBooks(
            null, popularGenres, null, null, null, PageRequest.of(0, limit)
        );
        List<BookDTO> books = genreBooks.getContent().stream()
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
        
        return List.of(RecommendationDTO.builder()
                .type("genre-based")
                .title("Popular Genres")
                .description("Books from popular genres like Fiction, Mystery, Romance, and Science Fiction")
                .books(books)
                .build());
    }
    
    private List<BookDTO> getPopularBooksInPopularGenres(int limit) {
        // Get most favorited books as a fallback
        Page<Object[]> mostFavorited = favoriteRepository.findMostFavoritedBooks(PageRequest.of(0, limit));
        List<Long> bookIds = mostFavorited.getContent().stream()
                .map(row -> (Long) row[0])
                .collect(Collectors.toList());
        
        if (bookIds.isEmpty()) {
            // If no favorites exist, return most reviewed books
            Page<Book> mostReviewed = bookRepository.findBooks(
                null, null, null, null, null, PageRequest.of(0, limit)
            );
            return mostReviewed.getContent().stream()
                    .map(EntityMapper::toBookDTO)
                    .collect(Collectors.toList());
        }
        
        // Get book details for most favorited books
        List<Book> books = bookRepository.findAllById(bookIds);
        return books.stream()
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
    }
}
