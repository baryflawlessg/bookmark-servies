package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.FavoriteDTO;
import com.bookverse.dto.PageResponse;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.UserDTO;
import com.bookverse.dto.UserUpdateDTO;
import com.bookverse.service.FavoriteService;
import com.bookverse.service.ReviewService;
import com.bookverse.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Users", description = "User profile management and favorites endpoints")
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get User by ID",
        description = "Retrieve user information by user ID"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User retrieved successfully",
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
                            "name": "John Doe",
                            "email": "john@example.com"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<UserDTO>> getUser(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get Current User Profile",
        description = "Retrieve the profile of the currently authenticated user"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User profile retrieved successfully",
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
                            "name": "John Doe",
                            "email": "john@example.com"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<UserDTO>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return userService.getCurrentUserProfile()
                    .map(user -> ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(user)))
                    .orElse(ResponseEntity.status(404).body(com.bookverse.dto.ApiResponse.error("User not found")));
        }
        return ResponseEntity.status(401).body(com.bookverse.dto.ApiResponse.error("Not authenticated"));
    }

    @PutMapping("/profile")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Update User Profile",
        description = "Update the current user's profile information (name, email, password)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Profile updated successfully",
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
                            "name": "John Smith",
                            "email": "john.smith@example.com"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Validation error or email already exists",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Validation Error",
                    value = """
                        {
                          "success": false,
                          "message": "Email already in use"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<UserDTO>> updateProfile(
            @io.swagger.v3.oas.annotations.Parameter(
                description = "Updated profile information",
                required = true,
                content = @io.swagger.v3.oas.annotations.media.Content(
                    examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "Update Profile Request",
                        value = """
                            {
                              "name": "John Smith",
                              "email": "john.smith@example.com",
                              "password": "newSecurePassword123"
                            }
                            """
                    )
                )
            )
            @Valid @RequestBody UserUpdateDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return userService.updateProfile(authentication.getName(), request)
                    .map(user -> ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(user)))
                    .orElse(ResponseEntity.status(404).body(com.bookverse.dto.ApiResponse.error("User not found")));
        }
        return ResponseEntity.status(401).body(com.bookverse.dto.ApiResponse.error("Not authenticated"));
    }

    @DeleteMapping("/profile")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Delete User Account",
        description = "Permanently delete the current user's account"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Account deleted successfully",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "success": true,
                          "message": "Account deleted successfully"
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Not authenticated"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<String>> deleteAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            boolean deleted = userService.deleteAccount(authentication.getName());
            if (deleted) {
                return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok("Account deleted successfully"));
            } else {
                return ResponseEntity.status(404).body(com.bookverse.dto.ApiResponse.error("User not found"));
            }
        }
        return ResponseEntity.status(401).body(com.bookverse.dto.ApiResponse.error("Not authenticated"));
    }

    @GetMapping("/{id}/reviews")
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
    public ResponseEntity<com.bookverse.dto.ApiResponse<PageResponse<ReviewDTO>>> getUserReviews(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(reviewService.getReviewsForUser(id, page, size)));
    }

    @GetMapping("/{id}/favorites")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get User Favorites",
        description = "Retrieve all favorite books for a specific user"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "User favorites retrieved successfully",
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
                              "bookTitle": "The Great Gatsby",
                              "bookAuthor": "F. Scott Fitzgerald",
                              "addedAt": "2024-01-15T10:30:00Z"
                            }
                          ]
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<List<FavoriteDTO>>> getUserFavorites(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(favoriteService.getFavorites(id)));
    }

    @PostMapping("/{id}/favorites/{bookId}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Add Book to Favorites",
        description = "Add a book to a user's favorites list"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book added to favorites successfully",
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
                            "bookTitle": "The Great Gatsby",
                            "bookAuthor": "F. Scott Fitzgerald",
                            "addedAt": "2024-01-15T10:30:00Z"
                          }
                        }
                        """
                )
            )
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Book already in favorites",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = com.bookverse.dto.ApiResponse.class),
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    name = "Already Favorited",
                    value = """
                        {
                          "success": false,
                          "message": "Already favorited"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<com.bookverse.dto.ApiResponse<FavoriteDTO>> addFavorite(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Book ID", example = "1", required = true)
            @PathVariable Long bookId) {
        return ResponseEntity.ok(com.bookverse.dto.ApiResponse.ok(favoriteService.addFavorite(id, bookId)));
    }

    @DeleteMapping("/{id}/favorites/{bookId}")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Remove Book from Favorites",
        description = "Remove a book from a user's favorites list"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "204",
            description = "Book removed from favorites successfully"
        )
    })
    public ResponseEntity<Void> removeFavorite(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID", example = "1", required = true)
            @PathVariable Long id,
            
            @io.swagger.v3.oas.annotations.Parameter(description = "Book ID", example = "1", required = true)
            @PathVariable Long bookId) {
        favoriteService.removeFavorite(id, bookId);
        return ResponseEntity.noContent().build();
    }
}
