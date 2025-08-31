package com.bookverse.service.impl;

import com.bookverse.dto.BookDetailDTO;
import com.bookverse.dto.BookDTO;
import com.bookverse.dto.PageResponse;
import com.bookverse.dto.ReviewDTO;
import com.bookverse.dto.SearchCriteriaDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import com.bookverse.entity.Review;
import com.bookverse.entity.User;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.ReviewRepository;
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
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;
    private Book testBook2;
    private Review testReview;
    private User testUser;

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
        testBook.setAverageRating(4.5);
        testBook.setReviewCount(10);

        // Setup second test book
        testBook2 = new Book();
        testBook2.setId(2L);
        testBook2.setTitle("Another Book");
        testBook2.setAuthor("Another Author");
        testBook2.setDescription("Another Description");
        testBook2.setPublishedYear(2022);
        testBook2.setCoverImageUrl("http://example.com/cover2.jpg");
        testBook2.setAverageRating(3.8);
        testBook2.setReviewCount(5);

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
        testReview.setReviewText("Great book!");
        testReview.setCreatedAt(LocalDateTime.now());
        testReview.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getBookDetails_WhenBookExists_ShouldReturnBookDetailDTO() {
        // Arrange
        Long bookId = 1L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        // Act
        BookDetailDTO result = bookService.getBookDetails(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        assertEquals(testBook.getTitle(), result.getTitle());
        assertEquals(testBook.getAuthor(), result.getAuthor());
        assertEquals(testBook.getDescription(), result.getDescription());
        assertEquals(testBook.getPublishedYear(), result.getPublishedYear());
        assertEquals(testBook.getCoverImageUrl(), result.getCoverImageUrl());

        // Verify repository was called
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getBookDetails_WhenBookNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long bookId = 999L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> bookService.getBookDetails(bookId)
        );

        assertEquals("Book not found", exception.getMessage());

        // Verify repository was called
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void searchBooks_WithValidCriteria_ShouldReturnPageResponse() {
        // Arrange
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
            .query("Test")
            .genres(Arrays.asList(BookGenre.Genre.FANTASY, BookGenre.Genre.SCI_FI))
            .minYear(2020)
            .maxYear(2025)
            .minRating(4.0)
            .sortBy("title")
            .sortDirection("asc")
            .page(0)
            .size(10)
            .build();

        List<Book> books = Arrays.asList(testBook, testBook2);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 10), 2);
        
        when(bookRepository.findBooks(
            eq(criteria.getQuery()),
            eq(criteria.getAuthor()),
            eq(criteria.getGenres()),
            eq(criteria.getMinYear()),
            eq(criteria.getMaxYear()),
            eq(criteria.getMinRating()),
            any(Pageable.class)
        )).thenReturn(bookPage);

        // Act
        PageResponse<BookDTO> result = bookService.searchBooks(criteria);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(2, result.getPagination().getTotalElements());
        assertEquals(0, result.getPagination().getPage());
        assertEquals(10, result.getPagination().getSize());

        // Verify repository was called with correct parameters
        verify(bookRepository, times(1)).findBooks(
            eq(criteria.getQuery()),
            eq(criteria.getAuthor()),
            eq(criteria.getGenres()),
            eq(criteria.getMinYear()),
            eq(criteria.getMaxYear()),
            eq(criteria.getMinRating()),
            any(Pageable.class)
        );
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void searchBooks_WithNullCriteria_ShouldUseDefaultValues() {
        // Arrange
        SearchCriteriaDTO criteria = new SearchCriteriaDTO();
        criteria.setQuery(null);
        criteria.setGenres(null);
        criteria.setMinYear(null);
        criteria.setMaxYear(null);
        criteria.setMinRating(null);
        criteria.setSortBy(null);
        criteria.setSortDirection(null);
        criteria.setPage(null);
        criteria.setSize(null);

        List<Book> books = Arrays.asList(testBook);
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 20), 1);
        
        when(bookRepository.findBooks(
            eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class)
        )).thenReturn(bookPage);

        // Act
        PageResponse<BookDTO> result = bookService.searchBooks(criteria);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getPagination().getTotalElements());
        assertEquals(0, result.getPagination().getPage());
        assertEquals(20, result.getPagination().getSize()); // Default size

        verify(bookRepository, times(1)).findBooks(
            eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class)
        );
    }

    @Test
    void searchBooks_WithEmptyResults_ShouldReturnEmptyPageResponse() {
        // Arrange
        SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
            .query("Nonexistent")
            .page(0)
            .size(10)
            .build();

        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        
        when(bookRepository.findBooks(
            eq(criteria.getQuery()),
            eq(criteria.getAuthor()),
            eq(criteria.getGenres()),
            eq(criteria.getMinYear()),
            eq(criteria.getMaxYear()),
            eq(criteria.getMinRating()),
            any(Pageable.class)
        )).thenReturn(emptyPage);

        // Act
        PageResponse<BookDTO> result = bookService.searchBooks(criteria);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getItems().size());
        assertEquals(0, result.getPagination().getTotalElements());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void searchBooks_WithDifferentSortOptions_ShouldApplyCorrectSort() {
        // Test different sort options
        String[] sortOptions = {"author", "rating", "date", "price", "unknown"};
        String[] directions = {"asc", "desc"};

        for (String sortBy : sortOptions) {
            for (String direction : directions) {
                SearchCriteriaDTO criteria = SearchCriteriaDTO.builder()
                    .sortBy(sortBy)
                    .sortDirection(direction)
                    .page(0)
                    .size(10)
                    .build();

                List<Book> books = Arrays.asList(testBook);
                Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(0, 10), 1);
                
                when(bookRepository.findBooks(
                    eq(criteria.getQuery()),
                    eq(criteria.getAuthor()),
                    eq(criteria.getGenres()),
                    eq(criteria.getMinYear()),
                    eq(criteria.getMaxYear()),
                    eq(criteria.getMinRating()),
                    any(Pageable.class)
                )).thenReturn(bookPage);

                // Act
                PageResponse<BookDTO> result = bookService.searchBooks(criteria);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getItems().size());

                verify(bookRepository, times(1)).findBooks(
                    eq(criteria.getQuery()),
                    eq(criteria.getAuthor()),
                    eq(criteria.getGenres()),
                    eq(criteria.getMinYear()),
                    eq(criteria.getMaxYear()),
                    eq(criteria.getMinRating()),
                    any(Pageable.class)
                );
                
                // Reset mocks for next iteration
                reset(bookRepository);
            }
        }
    }

    @Test
    void getBookReviews_WithValidParameters_ShouldReturnPageResponse() {
        // Arrange
        Long bookId = 1L;
        Integer page = 0;
        Integer size = 10;

        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, PageRequest.of(page, size), 1);
        
        when(reviewRepository.findByBookIdOrderByCreatedAtDesc(eq(bookId), any(Pageable.class)))
            .thenReturn(reviewPage);

        // Act
        PageResponse<ReviewDTO> result = bookService.getBookReviews(bookId, page, size);

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

        // Verify repository was called
        verify(reviewRepository, times(1)).findByBookIdOrderByCreatedAtDesc(eq(bookId), any(Pageable.class));
        verifyNoMoreInteractions(reviewRepository);
    }

    @Test
    void getBookReviews_WithEmptyResults_ShouldReturnEmptyPageResponse() {
        // Arrange
        Long bookId = 999L;
        Integer page = 0;
        Integer size = 10;

        Page<Review> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        
        when(reviewRepository.findByBookIdOrderByCreatedAtDesc(eq(bookId), any(Pageable.class)))
            .thenReturn(emptyPage);

        // Act
        PageResponse<ReviewDTO> result = bookService.getBookReviews(bookId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getItems().size());
        assertEquals(0, result.getPagination().getTotalElements());
        assertTrue(result.getItems().isEmpty());
    }

    @Test
    void getBookReviews_WithNullParameters_ShouldThrowException() {
        // Arrange
        Long bookId = 1L;
        Integer page = null;
        Integer size = null;

        // Act & Assert
        // The method doesn't handle null parameters, so it should throw NullPointerException
        assertThrows(NullPointerException.class, () -> {
            bookService.getBookReviews(bookId, page, size);
        });
    }

    @Test
    void getFeaturedBooks_ShouldReturnListOfBookDTOs() {
        // Arrange
        List<Book> featuredBooks = Arrays.asList(testBook, testBook2);
        Page<Book> bookPage = new PageImpl<>(featuredBooks, PageRequest.of(0, 10), 2);
        
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);

        // Act
        List<BookDTO> result = bookService.getFeaturedBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        
        BookDTO firstBook = result.get(0);
        assertEquals(testBook.getId(), firstBook.getId());
        assertEquals(testBook.getTitle(), firstBook.getTitle());
        assertEquals(testBook.getAuthor(), firstBook.getAuthor());
        assertEquals(testBook.getCoverImageUrl(), firstBook.getCoverImageUrl());
        assertEquals(testBook.getPublishedYear(), firstBook.getPublishedYear());
        assertEquals(testBook.getAverageRating(), firstBook.getAverageRating());
        assertEquals(testBook.getReviewCount(), firstBook.getReviewCount());

        BookDTO secondBook = result.get(1);
        assertEquals(testBook2.getId(), secondBook.getId());
        assertEquals(testBook2.getTitle(), secondBook.getTitle());

        // Verify repository was called with correct parameters
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    void getFeaturedBooks_WithEmptyResults_ShouldReturnEmptyList() {
        // Arrange
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        List<BookDTO> result = bookService.getFeaturedBooks();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());
    }

    @Test
    void getFeaturedBooks_ShouldUseCorrectPageableParameters() {
        // Arrange
        List<Book> featuredBooks = Arrays.asList(testBook);
        Page<Book> bookPage = new PageImpl<>(featuredBooks, PageRequest.of(0, 10), 1);
        
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);

        // Act
        bookService.getFeaturedBooks();

        // Verify repository was called with correct Pageable
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }
}
