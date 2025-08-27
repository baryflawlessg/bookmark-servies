package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> getByBook(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getReviewsForBook(bookId, page, size)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> getByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getReviewsForUser(userId, page, size)));
    }

    @PostMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> create(
            @PathVariable Long bookId,
            @RequestParam Long userId,
            @Valid @RequestBody ReviewCreateDTO request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.createReview(bookId, userId, request)));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> update(
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @Valid @RequestBody ReviewUpdateDTO request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.updateReview(reviewId, userId, request)));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            @RequestParam Long userId
    ) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}
