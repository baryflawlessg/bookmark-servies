package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    void getByBook_ShouldReturnBookReviews() {
        // Arrange
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setBookTitle("Test Book");
        PaginationDTO pagination = PaginationDTO.builder().page(0).size(20).totalElements(1).totalPages(1).first(true).last(false).build();
        PageResponse<ReviewDTO> pageResponse = PageResponse.of(Arrays.asList(review), pagination);
        when(reviewService.getReviewsForBook(1L, 0, 20)).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> response = reviewController.getByBook(1L, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getItems().size());
        verify(reviewService).getReviewsForBook(1L, 0, 20);
    }

    @Test
    void getByUser_ShouldReturnUserReviews() {
        // Arrange
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setBookTitle("Test Book");
        PaginationDTO pagination = PaginationDTO.builder().page(0).size(20).totalElements(1).totalPages(1).first(true).last(false).build();
        PageResponse<ReviewDTO> pageResponse = PageResponse.of(Arrays.asList(review), pagination);
        when(reviewService.getReviewsForUser(1L, 0, 20)).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> response = reviewController.getByUser(1L, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getItems().size());
        verify(reviewService).getReviewsForUser(1L, 0, 20);
    }

    @Test
    void create_ShouldReturnCreatedReview() {
        // Arrange
        ReviewCreateDTO createDTO = new ReviewCreateDTO();
        createDTO.setRating(5);
        createDTO.setReviewText("Great book!");

        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setRating(5);
        review.setReviewText("Great book!");

        when(reviewService.createReview(1L, 1L, createDTO)).thenReturn(review);

        // Act
        ResponseEntity<ApiResponse<ReviewDTO>> response = reviewController.create(1L, 1L, createDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(review, response.getBody().getData());
        verify(reviewService).createReview(1L, 1L, createDTO);
    }

    @Test
    void update_ShouldReturnUpdatedReview() {
        // Arrange
        ReviewUpdateDTO updateDTO = new ReviewUpdateDTO();
        updateDTO.setRating(4);
        updateDTO.setReviewText("Updated review");

        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setRating(4);
        review.setReviewText("Updated review");

        when(reviewService.updateReview(1L, 1L, updateDTO)).thenReturn(review);

        // Act
        ResponseEntity<ApiResponse<ReviewDTO>> response = reviewController.update(1L, 1L, updateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(review, response.getBody().getData());
        verify(reviewService).updateReview(1L, 1L, updateDTO);
    }

    @Test
    void delete_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = reviewController.delete(1L, 1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(reviewService).deleteReview(1L, 1L);
    }
}
