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
@io.swagger.v3.oas.annotations.tags.Tag(name = "Home", description = "Home page and landing page endpoints")
public class HomeController {

    private final BookService bookService;
    private final ReviewService reviewService;

    @GetMapping("/featured")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Featured Books",
        description = "Retrieve featured books for the home page carousel. Returns top-rated books with pagination."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Featured books retrieved successfully",
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
                                "title": "The Great Gatsby",
                                "author": "F. Scott Fitzgerald",
                                "description": "A story of the fabulously wealthy Jay Gatsby...",
                                "coverImageUrl": "https://example.com/gatsby.jpg",
                                "publishedYear": 1925,
                                "averageRating": 4.8,
                                "reviewCount": 1250
                              },
                              {
                                "id": 2,
                                "title": "To Kill a Mockingbird",
                                "author": "Harper Lee",
                                "description": "The story of young Scout Finch...",
                                "coverImageUrl": "https://example.com/mockingbird.jpg",
                                "publishedYear": 1960,
                                "averageRating": 4.7,
                                "reviewCount": 980
                              }
                            ],
                            "pagination": {
                              "page": 0,
                              "size": 10,
                              "totalElements": 50,
                              "totalPages": 5,
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
    public ResponseEntity<com.bookverse.dto.ApiResponse<PageResponse<BookDTO>>> featured(
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        // For MVP, reuse search with default sort
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder().page(page).size(size).build();
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(bookService.searchBooks(criteria)));
    }

    @GetMapping("/recent-reviews")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Recent Reviews",
        description = "Retrieve recent reviews for the home page. Shows latest community activity."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Recent reviews retrieved successfully",
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
                                "reviewText": "A masterpiece of American literature that captures the essence of the Jazz Age...",
                                "userName": "John Doe",
                                "createdAt": "2024-01-15T10:30:00Z"
                              },
                              {
                                "id": 2,
                                "bookTitle": "To Kill a Mockingbird",
                                "rating": 4,
                                "reviewText": "A powerful story about justice and growing up in the American South...",
                                "userName": "Jane Smith",
                                "createdAt": "2024-01-15T09:15:00Z"
                              }
                            ],
                            "pagination": {
                              "page": 0,
                              "size": 10,
                              "totalElements": 1250,
                              "totalPages": 125,
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
    public ResponseEntity<com.bookverse.dto.ApiResponse<PageResponse<ReviewDTO>>> recentReviews(
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        // For MVP, use a bookId of 0 to get empty or expose a dedicated endpoint later; here reuse by user 0
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(reviewService.getReviewsForUser(0L, page, size)));
    }
}
