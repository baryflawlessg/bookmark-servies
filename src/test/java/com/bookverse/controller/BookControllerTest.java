package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.entity.BookGenre;
import com.bookverse.service.BookService;
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
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void list_ShouldReturnBooks() {
        // Arrange
        BookDTO book = new BookDTO();
        book.setId(1L);
        book.setTitle("Test Book");
        PaginationDTO pagination = PaginationDTO.builder().page(0).size(20).totalElements(1).totalPages(1).first(true).last(false).build();
        PageResponse<BookDTO> pageResponse = PageResponse.of(Arrays.asList(book), pagination);
        when(bookService.searchBooks(any(SearchCriteriaDTO.class))).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponse<BookDTO>>> response = bookController.list(
            "test", "Test Author", Arrays.asList(BookGenre.Genre.ROMANCE), 1900, 2024, 4.0, "title", "asc", 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getItems().size());
        verify(bookService).searchBooks(any(SearchCriteriaDTO.class));
    }

    @Test
    void details_ShouldReturnBookDetails() {
        // Arrange
        BookDetailDTO bookDetail = new BookDetailDTO();
        bookDetail.setId(1L);
        bookDetail.setTitle("Test Book");
        when(bookService.getBookDetails(1L)).thenReturn(bookDetail);

        // Act
        ResponseEntity<ApiResponse<BookDetailDTO>> response = bookController.details(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(bookDetail, response.getBody().getData());
        verify(bookService).getBookDetails(1L);
    }

    @Test
    void getBookReviews_ShouldReturnReviews() {
        // Arrange
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setBookTitle("Test Book");
        PaginationDTO pagination = PaginationDTO.builder().page(0).size(10).totalElements(1).totalPages(1).first(true).last(false).build();
        PageResponse<ReviewDTO> pageResponse = PageResponse.of(Arrays.asList(review), pagination);
        when(bookService.getBookReviews(1L, 0, 10)).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> response = bookController.getBookReviews(1L, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getItems().size());
        verify(bookService).getBookReviews(1L, 0, 10);
    }

    @Test
    void getFeaturedBooks_ShouldReturnFeaturedBooks() {
        // Arrange
        BookDTO book = new BookDTO();
        book.setId(1L);
        book.setTitle("Featured Book");
        List<BookDTO> featuredBooks = Arrays.asList(book);
        when(bookService.getFeaturedBooks()).thenReturn(featuredBooks);

        // Act
        ResponseEntity<ApiResponse<List<BookDTO>>> response = bookController.getFeaturedBooks();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(bookService).getFeaturedBooks();
    }
}
