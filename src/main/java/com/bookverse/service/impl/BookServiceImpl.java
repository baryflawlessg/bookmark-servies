package com.bookverse.service.impl;

import com.bookverse.dto.BookDTO;
import com.bookverse.dto.BookDetailDTO;
import com.bookverse.dto.PageResponse;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.SearchCriteriaDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import com.bookverse.entity.Review;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.ReviewRepository;
import com.bookverse.service.BookService;
import com.bookverse.service.mapper.EntityMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public PageResponse<BookDTO> searchBooks(SearchCriteriaDTO criteria) {
        int page = criteria.getPage() != null ? criteria.getPage() : 0;
        int size = criteria.getSize() != null ? criteria.getSize() : 20;
        Sort sort = resolveSort(criteria.getSortBy(), criteria.getSortDirection());
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Book> result;
        String query = criteria.getQuery();
        List<BookGenre.Genre> genres = criteria.getGenres();

        if (genres != null && !genres.isEmpty()) {
            result = bookRepository.findByGenres(genres, pageable);
        } else if (query != null && !query.isBlank()) {
            result = bookRepository.findByTitleOrAuthorContainingIgnoreCase(query, pageable);
        } else if (criteria.getMinRating() != null) {
            result = bookRepository.findByAverageRatingGreaterThanEqual(criteria.getMinRating(), pageable);
        } else if (criteria.getMinYear() != null && criteria.getMaxYear() != null) {
            result = bookRepository.findByPublishedYearBetween(criteria.getMinYear(), criteria.getMaxYear(), pageable);
        } else {
            result = bookRepository.findAll(pageable);
        }

        List<BookDTO> items = result.getContent().stream().map(EntityMapper::toBookDTO).collect(Collectors.toList());
        return EntityMapper.toPageResponse(items, page, size, result.getTotalElements());
    }

    private Sort resolveSort(String sortBy, String direction) {
        String column = Objects.requireNonNullElse(sortBy, "title");
        Sort sort = switch (column) {
            case "author" -> Sort.by("author");
            case "rating" -> Sort.by(Sort.Order.desc("reviews.rating"));
            case "date" -> Sort.by(Sort.Order.desc("createdAt"));
            default -> Sort.by("title");
        };
        if ("desc".equalsIgnoreCase(direction)) {
            return sort.descending();
        }
        return sort.ascending();
    }

    @Override
    @Transactional
    public BookDetailDTO getBookDetails(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        return EntityMapper.toBookDetailDTO(book);
    }

    @Override
    public PageResponse<ReviewDTO> getBookReviews(Long bookId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviews = reviewRepository.findByBookIdOrderByCreatedAtDesc(bookId, pageable);
        
        List<ReviewDTO> items = reviews.getContent().stream()
                .map(EntityMapper::toReviewDTO)
                .collect(Collectors.toList());
        
        return EntityMapper.toPageResponse(items, page, size, reviews.getTotalElements());
    }

    @Override
    public List<BookDTO> getFeaturedBooks() {
        // Simple implementation: return top-rated books
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "averageRating"));
        Page<Book> featuredBooksPage = bookRepository.findTopRatedBooks(pageable);
        
        return featuredBooksPage.getContent().stream()
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
    }
}
