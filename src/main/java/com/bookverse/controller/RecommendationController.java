package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.RecommendationDTO;
import com.bookverse.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<RecommendationDTO>>> topRated(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.ok(recommendationService.getTopRated(limit)));
    }

    @GetMapping("/genre-based")
    public ResponseEntity<ApiResponse<List<RecommendationDTO>>> genreBased(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(recommendationService.getGenreBased(userId, limit)));
    }

    @GetMapping("/ai")
    public ResponseEntity<ApiResponse<List<RecommendationDTO>>> ai(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(recommendationService.getAiRecommendations(userId, limit)));
    }
}
