package com.bookverse.controller;

import com.bookverse.dto.ApiResponse;
import com.bookverse.dto.BookDTO;
import com.bookverse.dto.PageResponse;
import com.bookverse.dto.PaginationDTO;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.SearchCriteriaDTO;
import com.bookverse.service.BookService;
import com.bookverse.service.ReviewService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private HomeController homeController;

    @Test
    void featured_ShouldReturnFeaturedBooks() {
        // Arrange
        BookDTO book = new BookDTO();
        book.setId(1L);
        book.setTitle("Test Book");
        PaginationDTO pagination = PaginationDTO.builder().page(0).size(10).totalElements(1).totalPages(1).first(true).last(false).build();
        PageResponse<BookDTO> pageResponse = PageResponse.of(Arrays.asList(book), pagination);
        when(bookService.searchBooks(any(SearchCriteriaDTO.class))).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponse<BookDTO>>> response = homeController.featured(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getItems().size());
        verify(bookService).searchBooks(any(SearchCriteriaDTO.class));
    }

    @Test
    void recentReviews_ShouldReturnRecentReviews() {
        // Arrange
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setBookTitle("Test Book");
        PaginationDTO pagination = PaginationDTO.builder().page(0).size(10).totalElements(1).totalPages(1).first(true).last(false).build();
        PageResponse<ReviewDTO> pageResponse = PageResponse.of(Arrays.asList(review), pagination);
        when(reviewService.getReviewsForUser(0L, 0, 10)).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> response = homeController.recentReviews(0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getItems().size());
        verify(reviewService).getReviewsForUser(0L, 0, 10);
    }
}
