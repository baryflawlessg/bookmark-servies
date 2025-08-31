package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationControllerTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    void getAllRecommendations_WithUserId_ShouldReturnAllRecommendations() {
        // Arrange
        RecommendationDTO topRated = new RecommendationDTO();
        topRated.setType("top-rated");
        topRated.setTitle("Top Rated Books");

        RecommendationDTO userBased = new RecommendationDTO();
        userBased.setType("user-genre-based");
        userBased.setTitle("Because you like these genres");

        List<RecommendationDTO> topRatedList = Arrays.asList(topRated);
        List<RecommendationDTO> userBasedList = Arrays.asList(userBased);

        when(recommendationService.getTopRated(10)).thenReturn(topRatedList);
        when(recommendationService.getUserBasedRecommendations(1L, 10)).thenReturn(userBasedList);

        // Act
        ResponseEntity<ApiResponse<List<RecommendationDTO>>> response = recommendationController.getAllRecommendations(1L, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().size());
        verify(recommendationService).getTopRated(10);
        verify(recommendationService).getUserBasedRecommendations(1L, 10);
    }

    @Test
    void getAllRecommendations_WithoutUserId_ShouldReturnOnlyTopRated() {
        // Arrange
        RecommendationDTO topRated = new RecommendationDTO();
        topRated.setType("top-rated");
        topRated.setTitle("Top Rated Books");

        List<RecommendationDTO> topRatedList = Arrays.asList(topRated);
        when(recommendationService.getTopRated(10)).thenReturn(topRatedList);

        // Act
        ResponseEntity<ApiResponse<List<RecommendationDTO>>> response = recommendationController.getAllRecommendations(null, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(recommendationService).getTopRated(10);
        verifyNoMoreInteractions(recommendationService);
    }

    @Test
    void topRated_ShouldReturnTopRatedBooks() {
        // Arrange
        RecommendationDTO recommendation = new RecommendationDTO();
        recommendation.setType("top-rated");
        recommendation.setTitle("Top Rated Books");
        List<RecommendationDTO> recommendations = Arrays.asList(recommendation);
        when(recommendationService.getTopRated(10)).thenReturn(recommendations);

        // Act
        ResponseEntity<ApiResponse<List<RecommendationDTO>>> response = recommendationController.topRated(10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(recommendationService).getTopRated(10);
    }

    @Test
    void genreBased_ShouldReturnGenreBasedRecommendations() {
        // Arrange
        RecommendationDTO recommendation = new RecommendationDTO();
        recommendation.setType("genre-based");
        recommendation.setTitle("Popular Genres");
        List<RecommendationDTO> recommendations = Arrays.asList(recommendation);
        when(recommendationService.getGenreBased(10)).thenReturn(recommendations);

        // Act
        ResponseEntity<ApiResponse<List<RecommendationDTO>>> response = recommendationController.genreBased(10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(recommendationService).getGenreBased(10);
    }

    @Test
    void userBased_ShouldReturnUserBasedRecommendations() {
        // Arrange
        RecommendationDTO recommendation = new RecommendationDTO();
        recommendation.setType("user-genre-based");
        recommendation.setTitle("Because you like these genres");
        List<RecommendationDTO> recommendations = Arrays.asList(recommendation);
        when(recommendationService.getUserBasedRecommendations(1L, 10)).thenReturn(recommendations);

        // Act
        ResponseEntity<ApiResponse<List<RecommendationDTO>>> response = recommendationController.userBased(1L, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(recommendationService).getUserBasedRecommendations(1L, 10);
    }

    @Test
    void favoritesGenreBased_ShouldReturnFavoritesGenreBasedRecommendations() {
        // Arrange
        RecommendationDTO recommendation = new RecommendationDTO();
        recommendation.setType("favorites-genre-based");
        recommendation.setTitle("Based on your favorite genres");
        List<RecommendationDTO> recommendations = Arrays.asList(recommendation);
        when(recommendationService.getGenreBasedFromFavorites(1L, 10)).thenReturn(recommendations);

        // Act
        ResponseEntity<ApiResponse<List<RecommendationDTO>>> response = recommendationController.favoritesGenreBased(1L, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(recommendationService).getGenreBasedFromFavorites(1L, 10);
    }
}
