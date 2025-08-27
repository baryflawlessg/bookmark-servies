package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.FavoriteService;
import com.bookverse.service.ReviewService;
import com.bookverse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.ok(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> getUserReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.getReviewsForUser(id, page, size)));
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<ApiResponse<List<FavoriteDTO>>> getUserFavorites(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(favoriteService.getFavorites(id)));
    }

    @PostMapping("/{id}/favorites/{bookId}")
    public ResponseEntity<ApiResponse<FavoriteDTO>> addFavorite(@PathVariable Long id, @PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.ok(favoriteService.addFavorite(id, bookId)));
    }

    @DeleteMapping("/{id}/favorites/{bookId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long id, @PathVariable Long bookId) {
        favoriteService.removeFavorite(id, bookId);
        return ResponseEntity.noContent().build();
    }
}
