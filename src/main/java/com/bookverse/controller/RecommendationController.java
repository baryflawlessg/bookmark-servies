package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.RecommendationDTO;
import com.bookverse.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Recommendations", description = "Book recommendation endpoints for personalized suggestions")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get All Recommendations",
        description = "Retrieve both top-rated and user-based recommendations. User-based recommendations are included only when userId is provided."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Recommendations retrieved successfully",
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
                              "type": "top-rated",
                              "title": "Top Rated Books",
                              "description": "Books with the highest ratings from our community",
                              "books": [
                                {
                                  "id": 1,
                                  "title": "The Great Gatsby",
                                  "author": "F. Scott Fitzgerald",
                                  "coverImageUrl": "https://example.com/gatsby.jpg",
                                  "averageRating": 4.8,
                                  "reviewCount": 1250
                                }
                              ]
                            },
                            {
                              "type": "user-genre-based",
                              "title": "Because you like these genres",
                              "description": "Books in genres you've shown interest in",
                              "books": [
                                {
                                  "id": 2,
                                  "title": "To Kill a Mockingbird",
                                  "author": "Harper Lee",
                                  "coverImageUrl": "https://example.com/mockingbird.jpg",
                                  "averageRating": 4.7,
                                  "reviewCount": 980
                                }
                              ]
                            }
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<List<RecommendationDTO>>> getAllRecommendations(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID for personalized recommendations (optional)", example = "1")
            @RequestParam(required = false) Long userId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Number of recommendations to return", example = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RecommendationDTO> allRecommendations = new ArrayList<>();
        
        // Always include top-rated books
        allRecommendations.addAll(recommendationService.getTopRated(limit));
        
        // Include user-based recommendations if userId is provided
        if (userId != null) {
            allRecommendations.addAll(recommendationService.getUserBasedRecommendations(userId, limit));
        }
        
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(allRecommendations));
    }

    @GetMapping("/top-rated")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Top Rated Books",
        description = "Retrieve books with the highest average ratings from the community"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Top rated books retrieved successfully",
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
                              "type": "top-rated",
                              "title": "Top Rated Books",
                              "description": "Books with the highest ratings from our community",
                              "books": [
                                {
                                  "id": 1,
                                  "title": "The Great Gatsby",
                                  "author": "F. Scott Fitzgerald",
                                  "coverImageUrl": "https://example.com/gatsby.jpg",
                                  "averageRating": 4.8,
                                  "reviewCount": 1250
                                },
                                {
                                  "id": 2,
                                  "title": "To Kill a Mockingbird",
                                  "author": "Harper Lee",
                                  "coverImageUrl": "https://example.com/mockingbird.jpg",
                                  "averageRating": 4.7,
                                  "reviewCount": 980
                                }
                              ]
                            }
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<List<RecommendationDTO>>> topRated(
            @io.swagger.v3.oas.annotations.Parameter(description = "Number of top rated books to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(recommendationService.getTopRated(limit)));
    }

    @GetMapping("/genre-based")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Genre-Based Recommendations",
        description = "Retrieve books from popular genres (Fiction, Mystery, Romance, Science Fiction)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Genre-based recommendations retrieved successfully",
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
                              "type": "genre-based",
                              "title": "Popular Genres",
                              "description": "Books from popular genres like Fiction, Mystery, Romance, and Science Fiction",
                              "books": [
                                {
                                  "id": 3,
                                  "title": "1984",
                                  "author": "George Orwell",
                                  "coverImageUrl": "https://example.com/1984.jpg",
                                  "averageRating": 4.6,
                                  "reviewCount": 850
                                }
                              ]
                            }
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<List<RecommendationDTO>>> genreBased(
            @io.swagger.v3.oas.annotations.Parameter(description = "Number of genre-based recommendations to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(recommendationService.getGenreBased(limit)));
    }

    @GetMapping("/user-based")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get User-Based Recommendations",
        description = "Retrieve personalized recommendations based on user's favorite books and preferred genres"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User-based recommendations retrieved successfully",
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
                              "type": "user-genre-based",
                              "title": "Because you like these genres",
                              "description": "Books in genres you've shown interest in",
                              "books": [
                                {
                                  "id": 4,
                                  "title": "Pride and Prejudice",
                                  "author": "Jane Austen",
                                  "coverImageUrl": "https://example.com/pride.jpg",
                                  "averageRating": 4.5,
                                  "reviewCount": 720
                                }
                              ]
                            },
                            {
                              "type": "popular",
                              "title": "Popular Books",
                              "description": "Trending books in our community",
                              "books": [
                                {
                                  "id": 5,
                                  "title": "The Hobbit",
                                  "author": "J.R.R. Tolkien",
                                  "coverImageUrl": "https://example.com/hobbit.jpg",
                                  "averageRating": 4.4,
                                  "reviewCount": 650
                                }
                              ]
                            }
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<List<RecommendationDTO>>> userBased(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID for personalized recommendations", example = "1", required = true)
            @RequestParam Long userId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Number of user-based recommendations to return", example = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(recommendationService.getUserBasedRecommendations(userId, limit)));
    }

    @GetMapping("/favorites-genre-based")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Genre-Based Recommendations from Favorites",
        description = "Retrieve personalized recommendations based on genres of user's favorite books with weighted scoring"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Genre-based recommendations from favorites retrieved successfully",
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
                              "type": "favorites-genre-based",
                              "title": "Based on your favorite genres",
                              "description": "Books in genres you love",
                              "books": [
                                {
                                  "id": 1,
                                  "title": "The Great Gatsby",
                                  "author": "F. Scott Fitzgerald",
                                  "coverImageUrl": "https://example.com/gatsby.jpg",
                                  "averageRating": 4.8,
                                  "reviewCount": 1250
                                }
                              ]
                            }
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<List<RecommendationDTO>>> favoritesGenreBased(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID for personalized recommendations", example = "1", required = true)
            @RequestParam Long userId,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Number of recommendations to return", example = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(recommendationService.getGenreBasedFromFavorites(userId, limit)));
    }
}
