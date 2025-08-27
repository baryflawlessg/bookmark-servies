package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.BookService;
import com.bookverse.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<PageResponse<BookDTO>>> featured(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // For MVP, reuse search with default sort
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder().page(page).size(size).build();
        return ResponseEntity.ok(ApiResponse.ok(bookService.searchBooks(criteria)));
    }

    @GetMapping("/recent-reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> recentReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // For MVP, use a bookId of 0 to get empty or expose a dedicated endpoint later; here reuse by user 0
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getReviewsForUser(0L, page, size)));
    }
}
