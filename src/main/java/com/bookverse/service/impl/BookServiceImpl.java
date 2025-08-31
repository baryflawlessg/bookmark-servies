package com.bookverse.service.impl;

import com.bookverse.dto.BookDTO;
import com.bookverse.dto.BookDetailDTO;
import com.bookverse.dto.PageResponse;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.SearchCriteriaDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Review;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.ReviewRepository;
import com.bookverse.service.BookService;
import com.bookverse.service.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookDTO> searchBooks(SearchCriteriaDTO criteria) {
        int page = criteria.getPage() != null ? criteria.getPage() : 0;
        int size = criteria.getSize() != null ? criteria.getSize() : 20;
        Sort sort = resolveSort(criteria.getSortBy(), criteria.getSortDirection());
        Pageable pageable = PageRequest.of(page, size, sort);

        // Use the new findBooks method
        Page<Book> result = bookRepository.findBooks(
            criteria.getQuery(),
            criteria.getAuthor(),
            criteria.getGenres(),
            criteria.getMinYear(),
            criteria.getMaxYear(),
            criteria.getMinRating(),
            pageable
        );
        
        // Debug logging
        System.out.println("Found " + result.getTotalElements() + " books total");
        System.out.println("Page content size: " + result.getContent().size());
        if (!result.getContent().isEmpty()) {
            System.out.println("First book: " + result.getContent().get(0).getTitle());
        }

        List<BookDTO> items = result.getContent().stream().map(EntityMapper::toBookDTO).collect(Collectors.toList());
        
        // Debug logging
        System.out.println("Mapped to " + items.size() + " DTOs");
        if (!items.isEmpty()) {
            System.out.println("First DTO: " + items.get(0).getTitle());
        }
        
        return EntityMapper.toPageResponse(items, page, size, result.getTotalElements());
    }

    private Sort resolveSort(String sortBy, String direction) {
        String column = Objects.requireNonNullElse(sortBy, "title");
        Sort sort = switch (column.toLowerCase()) {
            case "author" -> Sort.by("author");
            case "rating" -> Sort.by("id"); // Use ID as fallback since rating requires special handling
            case "date", "publicationdate" -> Sort.by("publishedYear");
            case "price" -> Sort.by("id"); // Use ID as fallback since price field doesn't exist
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
        // Use simple findAll for featured books
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        Page<Book> featuredBooksPage = bookRepository.findAll(pageable);
        
        return featuredBooksPage.getContent().stream()
                .map(EntityMapper::toBookDTO)
                .collect(Collectors.toList());
    }
}
