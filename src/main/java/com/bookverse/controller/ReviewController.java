package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Reviews", description = "Book review management endpoints")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/book/{bookId}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Book Reviews",
        description = "Retrieve paginated reviews for a specific book"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book reviews retrieved successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "OK",
                          "data": {
                            "items": [
                              {
                                "id": 1,
                                "rating": 5,
                                "reviewText": "A masterpiece of American literature...",
                                "userName": "John Doe",
                                "createdAt": "2024-01-15T10:30:00Z"
                              }
                            ],
                            "pagination": {
                              "page": 0,
                              "size": 20,
                              "totalElements": 1250,
                              "totalPages": 63,
                              "first": true,
                              "last": false
                            }
                          }
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<PageResponse<ReviewDTO>>> getByBook(
            @io.swagger.v3.oas.annotations.Parameter(description = "Book ID", example = "1", required = true)
            @PathVariable Long bookId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(reviewService.getReviewsForBook(bookId, page, size)));
    }

    @GetMapping("/user/{userId}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get User Reviews",
        description = "Retrieve paginated review history for a specific user"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User reviews retrieved successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "OK",
                          "data": {
                            "items": [
                              {
                                "id": 1,
                                "bookTitle": "The Great Gatsby",
                                "rating": 5,
                                "reviewText": "A masterpiece of American literature...",
                                "createdAt": "2024-01-15T10:30:00Z"
                              }
                            ],
                            "pagination": {
                              "page": 0,
                              "size": 20,
                              "totalElements": 45,
                              "totalPages": 3,
                              "first": true,
                              "last": false
                            }
                          }
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<PageResponse<ReviewDTO>>> getByUser(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long userId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(reviewService.getReviewsForUser(userId, page, size)));
    }

    @PostMapping("/book/{bookId}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Create Book Review",
        description = "Create a new review for a specific book (requires authentication)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Review created successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "OK",
                          "data": {
                            "id": 1,
                            "rating": 5,
                            "reviewText": "A masterpiece of American literature...",
                            "userName": "John Doe",
                            "createdAt": "2024-01-15T10:30:00Z"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "success": false,
                          "message": "rating: Rating must be at least 1, reviewText: Review text must be between 10 and 2000 characters"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<ReviewDTO>> create(
            @io.swagger.v3.oas.annotations.Parameter(description = "Book ID", example = "1", required = true)
            @PathVariable Long bookId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @RequestParam Long userId,
            
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Review details",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Create Review Request",
                        value = """
                            {
                              "rating": 5,
                              "reviewText": "A masterpiece of American literature that captures the essence of the Jazz Age..."
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody ReviewCreateDTO request
    ) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(reviewService.createReview(bookId, userId, request)));
    }

    @PutMapping("/{reviewId}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Update Book Review",
        description = "Update an existing review (only by the review owner)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Review updated successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "OK",
                          "data": {
                            "id": 1,
                            "rating": 4,
                            "reviewText": "Updated review text...",
                            "userName": "John Doe",
                            "updatedAt": "2024-01-16T14:20:00Z"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Cannot edit a review you do not own",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Permission Denied",
                    value = """
                        {
                          "success": false,
                          "message": "Cannot edit a review you do not own"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<ReviewDTO>> update(
            @io.swagger.v3.oas.annotations.Parameter(description = "Review ID", example = "1", required = true)
            @PathVariable Long reviewId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @RequestParam Long userId,
            
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Updated review details",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Update Review Request",
                        value = """
                            {
                              "rating": 4,
                              "reviewText": "Updated review text with new thoughts..."
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody ReviewUpdateDTO request
    ) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(reviewService.updateReview(reviewId, userId, request)));
    }

    @DeleteMapping("/{reviewId}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Delete Book Review",
        description = "Delete a review (only by the review owner)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Review deleted successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Cannot delete a review you do not own",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Permission Denied",
                    value = """
                        {
                          "success": false,
                          "message": "Cannot delete a review you do not own"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<Void> delete(
            @io.swagger.v3.oas.annotations.Parameter(description = "Review ID", example = "1", required = true)
            @PathVariable Long reviewId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @RequestParam Long userId
    ) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}
