package com.bookverse.service;

import com.bookverse.dto.RecommendationDTO;

import java.util.List;

public interface RecommendationService {
    List<RecommendationDTO> getTopRated(int limit);
    List<RecommendationDTO> getGenreBased(Long userId, int limit);
    List<RecommendationDTO> getAiRecommendations(Long userId, int limit);
}
