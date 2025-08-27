package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.entity.BookGenre;
import com.bookverse.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BookDTO>>> list(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "genre", required = false) List<BookGenre.Genre> genres,
            @RequestParam(value = "minYear", required = false) Integer minYear,
            @RequestParam(value = "maxYear", required = false) Integer maxYear,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                .query(query)
                .genres(genres)
                .minYear(minYear)
                .maxYear(maxYear)
                .minRating(minRating)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .page(page)
                .size(size)
                .build();
        PageResponse<BookDTO> result = bookService.searchBooks(criteria);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookDetailDTO>> details(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.ok(bookService.getBookDetails(id)));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> getBookReviews(
            @PathVariable("id") Long bookId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        PageResponse<ReviewDTO> reviews = bookService.getBookReviews(bookId, page, size);
        return ResponseEntity.ok(ApiResponse.ok(reviews));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getFeaturedBooks() {
        List<BookDTO> featuredBooks = bookService.getFeaturedBooks();
        return ResponseEntity.ok(ApiResponse.ok(featuredBooks));
    }
}
