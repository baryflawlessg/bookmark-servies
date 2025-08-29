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
@io.swagger.v3.oas.annotations.tags.Tag(name = "Books", description = "Book management, search, and browsing endpoints")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Search and Filter Books",
        description = "Search books by title/author, filter by genre/rating/year, sort and paginate results"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Books retrieved successfully",
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
                                "averageRating": 4.5,
                                "reviewCount": 1250
                              }
                            ],
                            "pagination": {
                              "page": 0,
                              "size": 20,
                              "totalElements": 150,
                              "totalPages": 8,
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
    public ResponseEntity<com.bookverse.dto.ApiResponse<PageResponse<BookDTO>>> list(
            @io.swagger.v3.oas.annotations.Parameter(description = "Search query for title or author", example = "gatsby")
            @RequestParam(value = "query", required = false) String query,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Filter by book genres", example = "FICTION,MYSTERY")
            @RequestParam(value = "genre", required = false) List<BookGenre.Genre> genres,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Minimum published year", example = "1900")
            @RequestParam(value = "minYear", required = false) Integer minYear,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Maximum published year", example = "2024")
            @RequestParam(value = "maxYear", required = false) Integer maxYear,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Minimum average rating (1.0-5.0)", example = "4.0")
            @RequestParam(value = "minRating", required = false) Double minRating,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Sort field: title, author, rating, date", example = "rating")
            @RequestParam(value = "sortBy", required = false) String sortBy,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Sort direction: asc, desc", example = "desc")
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(value = "size", defaultValue = "20") Integer size
    ) {
        System.out.println("BookController.list() called with:");
        System.out.println("  sortBy: " + sortBy);
        System.out.println("  sortDirection: " + sortDirection);
        System.out.println("  page: " + page);
        System.out.println("  size: " + size);
        
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
        
        System.out.println("Calling bookService.searchBooks()...");
        PageResponse<BookDTO> result = bookService.searchBooks(criteria);
        System.out.println("bookService.searchBooks() returned: " + result.getItems().size() + " items");
        
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Book Details",
        description = "Retrieve detailed information about a specific book including reviews and genres"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book details retrieved successfully",
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
                            "title": "The Great Gatsby",
                            "author": "F. Scott Fitzgerald",
                            "description": "A story of the fabulously wealthy Jay Gatsby...",
                            "coverImageUrl": "https://example.com/gatsby.jpg",
                            "publishedYear": 1925,
                            "averageRating": 4.5,
                            "reviewCount": 1250,
                            "genres": ["FICTION", "CLASSIC"],
                            "reviews": [
                              {
                                "id": 1,
                                "rating": 5,
                                "reviewText": "A masterpiece of American literature...",
                                "userName": "John Doe",
                                "createdAt": "2024-01-15T10:30:00Z"
                              }
                            ]
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Book not found",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Book Not Found",
                    value = """
                        {
                          "success": false,
                          "message": "Book not found"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<BookDetailDTO>> details(
            @io.swagger.v3.oas.annotations.Parameter(description = "Book ID", example = "1", required = true)
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(bookService.getBookDetails(id)));
    }

    @GetMapping("/{id}/reviews")
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
    public ResponseEntity<com.bookverse.dto.ApiResponse<PageResponse<ReviewDTO>>> getBookReviews(
            @io.swagger.v3.oas.annotations.Parameter(description = "Book ID", example = "1", required = true)
            @PathVariable("id") Long bookId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size", example = "10")
            @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        PageResponse<ReviewDTO> reviews = bookService.getBookReviews(bookId, page, size);
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(reviews));
    }

    @GetMapping("/featured")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Featured Books",
        description = "Retrieve a list of featured books for the home page"
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
                          "data": [
                            {
                              "id": 1,
                              "title": "The Great Gatsby",
                              "author": "F. Scott Fitzgerald",
                              "coverImageUrl": "https://example.com/gatsby.jpg",
                              "averageRating": 4.5,
                              "reviewCount": 1250
                            }
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<List<BookDTO>>> getFeaturedBooks() {
        List<BookDTO> featuredBooks = bookService.getFeaturedBooks();
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(featuredBooks));
    }
}
