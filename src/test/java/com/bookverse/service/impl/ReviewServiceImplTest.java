package com.bookverse.service.impl;

import com.bookverse.dto.PageResponse;
import com.bookverse.dto.ReviewCreateDTO;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.ReviewUpdateDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Review;
import com.bookverse.entity.User;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.ReviewRepository;
import com.bookverse.repository.UserRepository;
import com.bookverse.service.mapper.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Book testBook;
    private User testUser;
    private Review testReview;
    private ReviewCreateDTO createRequest;
    private ReviewUpdateDTO updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setDescription("Test Description");
        testBook.setPublishedYear(2023);
        testBook.setCoverImageUrl("http://example.com/cover.jpg");
        testBook.setAverageRating(4.0);
        testBook.setReviewCount(5);

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        // Setup test review
        testReview = new Review();
        testReview.setId(1L);
        testReview.setBook(testBook);
        testReview.setUser(testUser);
        testReview.setRating(5);
        testReview.setReviewText("Great book! Highly recommended.");
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setUpdatedAt(LocalDateTime.now());

        // Setup create request
        createRequest = ReviewCreateDTO.builder()
            .rating(5)
            .reviewText("Excellent book! Must read.")
            .build();

        // Setup update request
        updateRequest = ReviewUpdateDTO.builder()
            .rating(4)
            .reviewText("Good book, but could be better.")
            .build();
    }

    @Test
    void createReview_WithValidData_ShouldReturnReviewDTO() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        ReviewDTO result = reviewService.createReview(bookId, userId, createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testReview.getId(), result.getId());
        assertEquals(testReview.getRating(), result.getRating());
        assertEquals(testReview.getReviewText(), result.getReviewText());
        assertEquals(testReview.getBook().getId(), result.getBookId());
        assertEquals(testReview.getUser().getId(), result.getUserId());

        // Verify repository calls
        verify(bookRepository, times(1)).findById(bookId);
        verify(userRepository, times(1)).findById(userId);
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(bookRepository, times(1)).save(any(Book.class));
        verifyNoMoreInteractions(reviewRepository, userRepository);
    }

    @Test
    void createReview_WhenBookNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long bookId = 999L;
        Long userId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.createReview(bookId, userId, createRequest)
        );

        assertEquals("Book not found", exception.getMessage());

        // Verify repository calls
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
        verifyNoInteractions(userRepository, reviewRepository);
    }

    @Test
    void createReview_WhenUserNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long bookId = 1L;
        Long userId = 999L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.createReview(bookId, userId, createRequest)
        );

        assertEquals("User not found", exception.getMessage());

        // Verify repository calls
        verify(bookRepository, times(1)).findById(bookId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(bookRepository, userRepository);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void createReview_ShouldUpdateBookRatingStats() {
        // Arrange
        Long bookId = 1L;
        Long userId = 1L;

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        reviewService.createReview(bookId, userId, createRequest);

        // Verify book was saved (which includes rating stats update)
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    void updateReview_WithValidData_ShouldReturnUpdatedReviewDTO() {
        // Arrange
        Long reviewId = 1L;
        Long userId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        ReviewDTO result = reviewService.updateReview(reviewId, userId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testReview.getId(), result.getId());
        assertEquals(updateRequest.getRating(), testReview.getRating());
        assertEquals(updateRequest.getReviewText(), testReview.getReviewText());

        // Verify repository calls
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(bookRepository, times(1)).save(any(Book.class));
        verifyNoMoreInteractions(reviewRepository, bookRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void updateReview_WhenReviewNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long reviewId = 999L;
        Long userId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.updateReview(reviewId, userId, updateRequest)
        );

        assertEquals("Review not found", exception.getMessage());

        // Verify repository calls
        verify(reviewRepository, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
        verifyNoInteractions(bookRepository, userRepository);
    }

    @Test
    void updateReview_WhenUserNotOwner_ShouldThrowSecurityException() {
        // Arrange
        Long reviewId = 1L;
        Long userId = 999L; // Different user ID

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // Act & Assert
        SecurityException exception = assertThrows(
            SecurityException.class,
            () -> reviewService.updateReview(reviewId, userId, updateRequest)
        );

        assertEquals("Cannot edit a review you do not own", exception.getMessage());

        // Verify repository calls
        verify(reviewRepository, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
        verifyNoInteractions(bookRepository, userRepository);
    }

    @Test
    void updateReview_ShouldUpdateBookRatingStats() {
        // Arrange
        Long reviewId = 1L;
        Long userId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        reviewService.updateReview(reviewId, userId, updateRequest);

        // Verify book was saved (which includes rating stats update)
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    void deleteReview_WithValidData_ShouldDeleteReview() {
        // Arrange
        Long reviewId = 1L;
        Long userId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        reviewService.deleteReview(reviewId, userId);

        // Verify repository calls
        verify(reviewRepository, times(1)).findById(reviewId);
        verify(reviewRepository, times(1)).delete(testReview);
        verify(bookRepository, times(1)).save(testBook);
        verifyNoMoreInteractions(reviewRepository, bookRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteReview_WhenReviewNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long reviewId = 999L;
        Long userId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reviewService.deleteReview(reviewId, userId)
        );

        assertEquals("Review not found", exception.getMessage());

        // Verify repository calls
        verify(reviewRepository, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
        verifyNoInteractions(bookRepository, userRepository);
    }

    @Test
    void deleteReview_WhenUserNotOwner_ShouldThrowSecurityException() {
        // Arrange
        Long reviewId = 1L;
        Long userId = 999L; // Different user ID

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));

        // Act & Assert
        SecurityException exception = assertThrows(
            SecurityException.class,
            () -> reviewService.deleteReview(reviewId, userId)
        );

        assertEquals("Cannot delete a review you do not own", exception.getMessage());

        // Verify repository calls
        verify(reviewRepository, times(1)).findById(reviewId);
        verifyNoMoreInteractions(reviewRepository);
        verifyNoInteractions(bookRepository, userRepository);
    }

    @Test
    void deleteReview_ShouldUpdateBookRatingStats() {
        // Arrange
        Long reviewId = 1L;
        Long userId = 1L;

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // Act
        reviewService.deleteReview(reviewId, userId);

        // Verify book was saved (which includes rating stats update)
        verify(bookRepository, times(1)).save(testBook);
    }

    @Test
    void getReviewsForBook_WithValidParameters_ShouldReturnPageResponse() {
        // Arrange
        Long bookId = 1L;
        int page = 0;
        int size = 10;

        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, size), 1);

        when(reviewRepository.findByBookIdOrderByCreatedAtDesc(eq(bookId), any(Pageable.class)))
            .thenReturn(reviewPage);

        // Act
        PageResponse<ReviewDTO> result = reviewService.getReviewsForBook(bookId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getPagination().getTotalElements());
        assertEquals(page, result.getPagination().getPage());
        assertEquals(size, result.getPagination().getSize());

        ReviewDTO reviewDTO = result.getItems().get(0);
        assertEquals(testReview.getId(), reviewDTO.getId());
        assertEquals(testReview.getRating(), reviewDTO.getRating());
        assertEquals(testReview.getReviewText(), reviewDTO.getReviewText());

        // Verify repository calls
        verify(reviewRepository, times(1)).findByBookIdOrderByCreatedAtDesc(eq(bookId), any(Pageable.class));
        verifyNoMoreInteractions(reviewRepository);
        verifyNoInteractions(bookRepository, userRepository);
    }

    @Test
    void getReviewsForBook_WithEmptyResults_ShouldReturnEmptyPageResponse() {
        // Arrange
        Long bookId = 999L;
        int page = 0;
        int size = 10;

        Page<Review> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);

        when(reviewRepository.findByBookIdOrderByCreatedAtDesc(eq(bookId), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Act
        PageResponse<ReviewDTO> result = reviewService.getReviewsForBook(bookId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getItems().size());
        assertEquals(0, result.getPagination().getTotalElements());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getReviewsForBook_WithMultipleReviews_ShouldReturnAllReviews() {
        // Arrange
        Long bookId = 1L;
        int page = 0;
        int size = 10;

        Review review2 = new Review();
        review2.setId(2L);
        review2.setBook(testBook);
        review2.setUser(testUser);
        review2.setRating(4);
        review2.setReviewText("Good book!");
        review2.setCreatedAt(LocalDateTime.now());
        review2.setUpdatedAt(LocalDateTime.now());

        List<Review> reviews = Arrays.asList(testReview, review2);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, size), 2);

        when(reviewRepository.findByBookIdOrderByCreatedAtDesc(eq(bookId), any(Pageable.class)))
            .thenReturn(reviewPage);

        // Act
        PageResponse<ReviewDTO> result = reviewService.getReviewsForBook(bookId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(2, result.getPagination().getTotalElements());

        ReviewDTO firstReview = result.getItems().get(0);
        assertEquals(testReview.getId(), firstReview.getId());
        assertEquals(testReview.getRating(), firstReview.getRating());

        ReviewDTO secondReview = result.getItems().get(1);
        assertEquals(review2.getId(), secondReview.getId());
        assertEquals(review2.getRating(), secondReview.getRating());
    }

    @Test
    void getReviewsForUser_WithValidParameters_ShouldReturnPageResponse() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        int size = 10;

        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, size), 1);

        when(reviewRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class)))
            .thenReturn(reviewPage);

        // Act
        PageResponse<ReviewDTO> result = reviewService.getReviewsForUser(userId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getPagination().getTotalElements());
        assertEquals(page, result.getPagination().getPage());
        assertEquals(size, result.getPagination().getSize());

        ReviewDTO reviewDTO = result.getItems().get(0);
        assertEquals(testReview.getId(), reviewDTO.getId());
        assertEquals(testReview.getRating(), reviewDTO.getRating());
        assertEquals(testReview.getReviewText(), reviewDTO.getReviewText());

        // Verify repository calls
        verify(reviewRepository, times(1)).findByUserIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class));
        verifyNoMoreInteractions(reviewRepository);
        verifyNoInteractions(bookRepository, userRepository);
    }

    @Test
    void getReviewsForUser_WithEmptyResults_ShouldReturnEmptyPageResponse() {
        // Arrange
        Long userId = 999L;
        int page = 0;
        int size = 10;

        Page<Review> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);

        when(reviewRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Act
        PageResponse<ReviewDTO> result = reviewService.getReviewsForUser(userId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getItems().size());
        assertEquals(0, result.getPagination().getTotalElements());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getReviewsForUser_WithMultipleReviews_ShouldReturnAllReviews() {
        // Arrange
        Long userId = 1L;
        int page = 0;
        int size = 10;

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Another Book");
        book2.setAuthor("Another Author");

        Review review2 = new Review();
        review2.setId(2L);
        review2.setBook(book2);
        review2.setUser(testUser);
        review2.setRating(3);
        review2.setReviewText("Average book.");
        review2.setCreatedAt(LocalDateTime.now());
        review2.setUpdatedAt(LocalDateTime.now());

        List<Review> reviews = Arrays.asList(testReview, review2);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, size), 2);

        when(reviewRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class)))
            .thenReturn(reviewPage);

        // Act
        PageResponse<ReviewDTO> result = reviewService.getReviewsForUser(userId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(2, result.getPagination().getTotalElements());

        ReviewDTO firstReview = result.getItems().get(0);
        assertEquals(testReview.getId(), firstReview.getId());
        assertEquals(testReview.getBook().getId(), firstReview.getBookId());

        ReviewDTO secondReview = result.getItems().get(1);
        assertEquals(review2.getId(), secondReview.getId());
        assertEquals(review2.getBook().getId(), secondReview.getBookId());
    }

    @Test
    void getReviewsForUser_WithPagination_ShouldUseCorrectPageable() {
        // Arrange
        Long userId = 1L;
        int page = 2;
        int size = 5;

        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, size), 1);

        when(reviewRepository.findByUserIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class)))
            .thenReturn(reviewPage);

        // Act
        PageResponse<ReviewDTO> result = reviewService.getReviewsForUser(userId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(page, result.getPagination().getPage());
        assertEquals(size, result.getPagination().getSize());

        // Verify repository was called with correct Pageable
        verify(reviewRepository, times(1)).findByUserIdOrderByCreatedAtDesc(eq(userId), any(Pageable.class));
    }
}
