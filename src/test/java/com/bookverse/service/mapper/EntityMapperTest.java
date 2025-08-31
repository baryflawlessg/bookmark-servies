package com.bookverse.service.mapper;

import com.bookverse.dto.*;
import com.bookverse.entity.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityMapperTest {

    @Test
    void toUserDTO_WithValidUser_ShouldReturnUserDTO() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        // Act
        UserDTO result = EntityMapper.toUserDTO(user);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void toUserDTO_WithNullUser_ShouldReturnNull() {
        // Act
        UserDTO result = EntityMapper.toUserDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toUserDTO_WithUserHavingNullFields_ShouldHandleGracefully() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName(null);
        user.setEmail(null);

        // Act
        UserDTO result = EntityMapper.toUserDTO(user);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getName());
        assertNull(result.getEmail());
    }

    @Test
    void toBookDTO_WithValidBook_ShouldReturnBookDTO() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setDescription("Test Description");
        book.setCoverImageUrl("http://example.com/cover.jpg");
        book.setPublishedYear(2023);
        book.setAverageRating(4.5);
        book.setReviewCount(10);
        book.setGenres(Arrays.asList());

        // Act
        BookDTO result = EntityMapper.toBookDTO(book);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals("http://example.com/cover.jpg", result.getCoverImageUrl());
        assertEquals(2023, result.getPublishedYear());
        assertEquals(4.5, result.getAverageRating());
        assertEquals(10, result.getReviewCount());
        assertNotNull(result.getGenres());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    void toBookDTO_WithNullBook_ShouldReturnNull() {
        // Act
        BookDTO result = EntityMapper.toBookDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toBookDTO_WithBookHavingNullFields_ShouldHandleGracefully() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book"); // Required field
        book.setAuthor("Test Author"); // Required field
        book.setDescription(null);
        book.setCoverImageUrl(null);
        book.setPublishedYear(2023); // Required field
        book.setAverageRating(null);
        book.setReviewCount(null);
        book.setGenres(null);

        // Act
        BookDTO result = EntityMapper.toBookDTO(book);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertNull(result.getCoverImageUrl());
        assertEquals(2023, result.getPublishedYear());
        assertEquals(0.0, result.getAverageRating());
        assertEquals(0, result.getReviewCount());
        assertNotNull(result.getGenres());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    void toBookDTO_WithBookHavingGenres_ShouldMapGenresCorrectly() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setGenres(Arrays.asList(
            createBookGenre(BookGenre.Genre.ROMANCE),
            createBookGenre(BookGenre.Genre.MYSTERY)
        ));

        // Act
        BookDTO result = EntityMapper.toBookDTO(book);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getGenres().size());
        assertTrue(result.getGenres().contains("ROMANCE"));
        assertTrue(result.getGenres().contains("MYSTERY"));
    }

    @Test
    void toBookDTO_WithBookHavingNullGenres_ShouldReturnEmptyList() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setGenres(null);

        // Act
        BookDTO result = EntityMapper.toBookDTO(book);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGenres());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    void toBookDTO_WithBookHavingGenresWithNullGenre_ShouldFilterOutNullGenres() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        
        BookGenre validGenre = createBookGenre(BookGenre.Genre.ROMANCE);
        BookGenre nullGenre = new BookGenre();
        nullGenre.setId(2L);
        nullGenre.setGenre(null);
        nullGenre.setBook(book);
        
        book.setGenres(Arrays.asList(validGenre, nullGenre));

        // Act
        BookDTO result = EntityMapper.toBookDTO(book);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getGenres().size());
        assertTrue(result.getGenres().contains("ROMANCE"));
    }

    @Test
    void toBookDetailDTO_WithValidBook_ShouldReturnBookDetailDTO() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setDescription("Test Description");
        book.setCoverImageUrl("http://example.com/cover.jpg");
        book.setPublishedYear(2023);
        book.setAverageRating(4.5);
        book.setReviewCount(10);
        book.setGenres(Arrays.asList());

        // Act
        BookDetailDTO result = EntityMapper.toBookDetailDTO(book);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals("Test Description", result.getDescription());
        assertEquals("http://example.com/cover.jpg", result.getCoverImageUrl());
        assertEquals(2023, result.getPublishedYear());
        assertEquals(4.5, result.getAverageRating());
        assertEquals(10, result.getReviewCount());
        assertNotNull(result.getGenres());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    void toBookDetailDTO_WithNullBook_ShouldReturnNull() {
        // Act
        BookDetailDTO result = EntityMapper.toBookDetailDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toReviewDTO_WithValidReview_ShouldReturnReviewDTO() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Test Book");

        Review review = new Review();
        review.setId(3L);
        review.setUser(user);
        review.setBook(book);
        review.setRating(5);
        review.setReviewText("Great book!");
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        // Act
        ReviewDTO result = EntityMapper.toReviewDTO(review);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(2L, result.getBookId());
        assertEquals("Test Book", result.getBookTitle());
        assertEquals(1L, result.getUserId());
        assertEquals("John Doe", result.getUserName());
        assertEquals(5, result.getRating());
        assertEquals("Great book!", result.getReviewText());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    void toReviewDTO_WithNullReview_ShouldReturnNull() {
        // Act
        ReviewDTO result = EntityMapper.toReviewDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toReviewDTO_WithReviewHavingNullBook_ShouldHandleGracefully() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Review review = new Review();
        review.setId(3L);
        review.setUser(user);
        review.setBook(null);
        review.setRating(5);
        review.setReviewText("Great book!");
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        // Act
        ReviewDTO result = EntityMapper.toReviewDTO(review);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertNull(result.getBookId());
        assertNull(result.getBookTitle());
        assertEquals(1L, result.getUserId());
        assertEquals("John Doe", result.getUserName());
        assertEquals(5, result.getRating());
        assertEquals("Great book!", result.getReviewText());
    }

    @Test
    void toReviewDTO_WithReviewHavingNullUser_ShouldHandleGracefully() {
        // Arrange
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Test Book");

        Review review = new Review();
        review.setId(3L);
        review.setUser(null);
        review.setBook(book);
        review.setRating(5);
        review.setReviewText("Great book!");
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        // Act
        ReviewDTO result = EntityMapper.toReviewDTO(review);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(2L, result.getBookId());
        assertEquals("Test Book", result.getBookTitle());
        assertNull(result.getUserId());
        assertNull(result.getUserName());
        assertEquals(5, result.getRating());
        assertEquals("Great book!", result.getReviewText());
    }

    @Test
    void toReviewDTO_WithReviewHavingNullBookAndUser_ShouldHandleGracefully() {
        // Arrange
        Review review = new Review();
        review.setId(3L);
        review.setUser(null);
        review.setBook(null);
        review.setRating(5);
        review.setReviewText("Great book!");
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        // Act
        ReviewDTO result = EntityMapper.toReviewDTO(review);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertNull(result.getBookId());
        assertNull(result.getBookTitle());
        assertNull(result.getUserId());
        assertNull(result.getUserName());
        assertEquals(5, result.getRating());
        assertEquals("Great book!", result.getReviewText());
    }

    @Test
    void toReviewDTO_WithReviewHavingBookWithNullFields_ShouldHandleGracefully() {
        // Arrange
        Book book = new Book();
        book.setId(2L);
        book.setTitle(null);

        Review review = new Review();
        review.setId(3L);
        review.setBook(book);
        review.setRating(5);
        review.setReviewText("Great book!");

        // Act
        ReviewDTO result = EntityMapper.toReviewDTO(review);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getBookId());
        assertNull(result.getBookTitle());
    }

    @Test
    void toReviewDTO_WithReviewHavingUserWithNullFields_ShouldHandleGracefully() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName(null);

        Review review = new Review();
        review.setId(3L);
        review.setUser(user);
        review.setRating(5);
        review.setReviewText("Great book!");

        // Act
        ReviewDTO result = EntityMapper.toReviewDTO(review);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertNull(result.getUserName());
    }

    @Test
    void toFavoriteDTO_WithValidFavorite_ShouldReturnFavoriteDTO() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Book book = new Book();
        book.setId(2L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setCoverImageUrl("http://example.com/cover.jpg");

        Favorite favorite = new Favorite();
        favorite.setId(3L);
        favorite.setUser(user);
        favorite.setBook(book);
        favorite.setCreatedAt(LocalDateTime.now());

        // Act
        FavoriteDTO result = EntityMapper.toFavoriteDTO(favorite);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(1L, result.getUserId());
        assertEquals(2L, result.getBookId());
        assertEquals("Test Book", result.getBookTitle());
        assertEquals("Test Author", result.getBookAuthor());
        assertEquals("http://example.com/cover.jpg", result.getCoverImageUrl());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void toFavoriteDTO_WithNullFavorite_ShouldReturnNull() {
        // Act
        FavoriteDTO result = EntityMapper.toFavoriteDTO(null);

        // Assert
        assertNull(result);
    }

    @Test
    void toFavoriteDTO_WithFavoriteHavingNullUser_ShouldHandleGracefully() {
        // Arrange
        Book book = new Book();
        book.setId(2L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setCoverImageUrl("http://example.com/cover.jpg");

        Favorite favorite = new Favorite();
        favorite.setId(3L);
        favorite.setUser(null);
        favorite.setBook(book);
        favorite.setCreatedAt(LocalDateTime.now());

        // Act
        FavoriteDTO result = EntityMapper.toFavoriteDTO(favorite);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertNull(result.getUserId());
        assertEquals(2L, result.getBookId());
        assertEquals("Test Book", result.getBookTitle());
        assertEquals("Test Author", result.getBookAuthor());
        assertEquals("http://example.com/cover.jpg", result.getCoverImageUrl());
    }

    @Test
    void toFavoriteDTO_WithFavoriteHavingNullBook_ShouldHandleGracefully() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");

        Favorite favorite = new Favorite();
        favorite.setId(3L);
        favorite.setUser(user);
        favorite.setBook(null);
        favorite.setCreatedAt(LocalDateTime.now());

        // Act
        FavoriteDTO result = EntityMapper.toFavoriteDTO(favorite);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(1L, result.getUserId());
        assertNull(result.getBookId());
        assertNull(result.getBookTitle());
        assertNull(result.getBookAuthor());
        assertNull(result.getCoverImageUrl());
    }

    @Test
    void toFavoriteDTO_WithFavoriteHavingNullUserAndBook_ShouldHandleGracefully() {
        // Arrange
        Favorite favorite = new Favorite();
        favorite.setId(3L);
        favorite.setUser(null);
        favorite.setBook(null);
        favorite.setCreatedAt(LocalDateTime.now());

        // Act
        FavoriteDTO result = EntityMapper.toFavoriteDTO(favorite);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertNull(result.getUserId());
        assertNull(result.getBookId());
        assertNull(result.getBookTitle());
        assertNull(result.getBookAuthor());
        assertNull(result.getCoverImageUrl());
    }

    @Test
    void toFavoriteDTO_WithFavoriteHavingBookWithNullFields_ShouldHandleGracefully() {
        // Arrange
        Book book = new Book();
        book.setId(2L);
        book.setTitle(null);
        book.setAuthor(null);
        book.setCoverImageUrl(null);

        Favorite favorite = new Favorite();
        favorite.setId(3L);
        favorite.setBook(book);
        favorite.setCreatedAt(LocalDateTime.now());

        // Act
        FavoriteDTO result = EntityMapper.toFavoriteDTO(favorite);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getBookId());
        assertNull(result.getBookTitle());
        assertNull(result.getBookAuthor());
        assertNull(result.getCoverImageUrl());
    }

    @Test
    void toPageResponse_WithValidParameters_ShouldReturnPageResponse() {
        // Arrange
        List<String> items = Arrays.asList("item1", "item2", "item3");
        int page = 0;
        int size = 10;
        long total = 25;

        // Act
        PageResponse<String> result = EntityMapper.toPageResponse(items, page, size, total);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getItems().size());
        assertEquals("item1", result.getItems().get(0));
        assertEquals("item2", result.getItems().get(1));
        assertEquals("item3", result.getItems().get(2));
        
        PaginationDTO pagination = result.getPagination();
        assertNotNull(pagination);
        assertEquals(0, pagination.getPage());
        assertEquals(10, pagination.getSize());
        assertEquals(25, pagination.getTotalElements());
        assertEquals(3, pagination.getTotalPages());
        assertTrue(pagination.isFirst());
        assertFalse(pagination.isLast());
    }

    @Test
    void toPageResponse_WithLastPage_ShouldSetLastToTrue() {
        // Arrange
        List<String> items = Arrays.asList("item1", "item2");
        int page = 2;
        int size = 10;
        long total = 25;

        // Act
        PageResponse<String> result = EntityMapper.toPageResponse(items, page, size, total);

        // Assert
        assertNotNull(result);
        PaginationDTO pagination = result.getPagination();
        assertEquals(2, pagination.getPage());
        assertEquals(3, pagination.getTotalPages());
        assertFalse(pagination.isFirst());
        assertTrue(pagination.isLast());
    }

    @Test
    void toPageResponse_WithEmptyItems_ShouldHandleGracefully() {
        // Arrange
        List<String> items = Arrays.asList();
        int page = 0;
        int size = 10;
        long total = 0;

        // Act
        PageResponse<String> result = EntityMapper.toPageResponse(items, page, size, total);

        // Assert
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        
        PaginationDTO pagination = result.getPagination();
        assertEquals(0, pagination.getPage());
        assertEquals(10, pagination.getSize());
        assertEquals(0, pagination.getTotalElements());
        assertEquals(0, pagination.getTotalPages());
        assertTrue(pagination.isFirst());
        assertTrue(pagination.isLast());
    }

    @Test
    void toPageResponse_WithSinglePage_ShouldSetFirstAndLastToTrue() {
        // Arrange
        List<String> items = Arrays.asList("item1", "item2");
        int page = 0;
        int size = 10;
        long total = 2;

        // Act
        PageResponse<String> result = EntityMapper.toPageResponse(items, page, size, total);

        // Assert
        assertNotNull(result);
        PaginationDTO pagination = result.getPagination();
        assertEquals(0, pagination.getPage());
        assertEquals(10, pagination.getSize());
        assertEquals(2, pagination.getTotalElements());
        assertEquals(1, pagination.getTotalPages());
        assertTrue(pagination.isFirst());
        assertTrue(pagination.isLast());
    }

    private BookGenre createBookGenre(BookGenre.Genre genre) {
        BookGenre bookGenre = new BookGenre();
        bookGenre.setId(1L);
        bookGenre.setGenre(genre);
        return bookGenre;
    }
}
