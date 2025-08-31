package com.bookverse.service.impl;

import com.bookverse.dto.FavoriteDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.Favorite;
import com.bookverse.entity.User;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
import com.bookverse.repository.UserRepository;
import com.bookverse.service.mapper.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceImplTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private User testUser;
    private Book testBook;
    private Book testBook2;
    private Favorite testFavorite;
    private Favorite testFavorite2;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

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

        // Setup test favorite
        testFavorite = new Favorite();
        testFavorite.setId(1L);
        testFavorite.setUser(testUser);
        testFavorite.setBook(testBook);
        testFavorite.setCreatedAt(LocalDateTime.now());

        // Setup second test favorite
        testFavorite2 = new Favorite();
        testFavorite2.setId(2L);
        testFavorite2.setUser(testUser);
        testFavorite2.setBook(testBook2);
        testFavorite2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void addFavorite_WithValidData_ShouldReturnFavoriteDTO() {
        // Arrange
        Long userId = 1L;
        Long bookId = 1L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(testFavorite);

        // Act
        FavoriteDTO result = favoriteService.addFavorite(userId, bookId);

        // Assert
        assertNotNull(result);
        assertEquals(testFavorite.getId(), result.getId());
        assertEquals(testUser.getId(), result.getUserId());
        assertEquals(testBook.getId(), result.getBookId());
        assertEquals(testBook.getTitle(), result.getBookTitle());
        assertEquals(testBook.getAuthor(), result.getBookAuthor());
        assertEquals(testBook.getCoverImageUrl(), result.getCoverImageUrl());
        assertEquals(testFavorite.getCreatedAt(), result.getCreatedAt());

        // Verify repository calls
        verify(favoriteRepository, times(1)).existsByUserIdAndBookId(userId, bookId);
        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(bookId);
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void addFavorite_WhenAlreadyFavorited_ShouldThrowIllegalStateException() {
        // Arrange
        Long userId = 1L;
        Long bookId = 1L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> favoriteService.addFavorite(userId, bookId)
        );

        assertEquals("Already favorited", exception.getMessage());

        // Verify repository calls
        verify(favoriteRepository, times(1)).existsByUserIdAndBookId(userId, bookId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void addFavorite_WhenUserNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long userId = 999L;
        Long bookId = 1L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> favoriteService.addFavorite(userId, bookId)
        );

        assertEquals("User not found", exception.getMessage());

        // Verify repository calls
        verify(favoriteRepository, times(1)).existsByUserIdAndBookId(userId, bookId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void addFavorite_WhenBookNotFound_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long userId = 1L;
        Long bookId = 999L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> favoriteService.addFavorite(userId, bookId)
        );

        assertEquals("Book not found", exception.getMessage());

        // Verify repository calls
        verify(favoriteRepository, times(1)).existsByUserIdAndBookId(userId, bookId);
        verify(userRepository, times(1)).findById(userId);
        verify(bookRepository, times(1)).findById(bookId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void removeFavorite_WithValidData_ShouldDeleteFavorite() {
        // Arrange
        Long userId = 1L;
        Long bookId = 1L;

        // Act
        favoriteService.removeFavorite(userId, bookId);

        // Assert
        verify(favoriteRepository, times(1)).deleteByUserIdAndBookId(userId, bookId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void removeFavorite_WithNonExistentFavorite_ShouldNotThrowException() {
        // Arrange
        Long userId = 999L;
        Long bookId = 999L;

        // Act & Assert
        assertDoesNotThrow(() -> favoriteService.removeFavorite(userId, bookId));

        // Verify repository calls
        verify(favoriteRepository, times(1)).deleteByUserIdAndBookId(userId, bookId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void getFavorites_WithValidUserId_ShouldReturnListOfFavoriteDTOs() {
        // Arrange
        Long userId = 1L;
        List<Favorite> favorites = Arrays.asList(testFavorite, testFavorite2);
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(favorites);

        // Act
        List<FavoriteDTO> result = favoriteService.getFavorites(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify first favorite
        FavoriteDTO firstFavorite = result.get(0);
        assertEquals(testFavorite.getId(), firstFavorite.getId());
        assertEquals(testUser.getId(), firstFavorite.getUserId());
        assertEquals(testBook.getId(), firstFavorite.getBookId());
        assertEquals(testBook.getTitle(), firstFavorite.getBookTitle());
        assertEquals(testBook.getAuthor(), firstFavorite.getBookAuthor());
        assertEquals(testBook.getCoverImageUrl(), firstFavorite.getCoverImageUrl());
        assertEquals(testFavorite.getCreatedAt(), firstFavorite.getCreatedAt());

        // Verify second favorite
        FavoriteDTO secondFavorite = result.get(1);
        assertEquals(testFavorite2.getId(), secondFavorite.getId());
        assertEquals(testUser.getId(), secondFavorite.getUserId());
        assertEquals(testBook2.getId(), secondFavorite.getBookId());
        assertEquals(testBook2.getTitle(), secondFavorite.getBookTitle());
        assertEquals(testBook2.getAuthor(), secondFavorite.getBookAuthor());
        assertEquals(testBook2.getCoverImageUrl(), secondFavorite.getCoverImageUrl());
        assertEquals(testFavorite2.getCreatedAt(), secondFavorite.getCreatedAt());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void getFavorites_WithNoFavorites_ShouldReturnEmptyList() {
        // Arrange
        Long userId = 1L;
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(Collections.emptyList());

        // Act
        List<FavoriteDTO> result = favoriteService.getFavorites(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void getFavorites_WithNonExistentUser_ShouldReturnEmptyList() {
        // Arrange
        Long userId = 999L;
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(Collections.emptyList());

        // Act
        List<FavoriteDTO> result = favoriteService.getFavorites(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void isFavorited_WhenFavorited_ShouldReturnTrue() {
        // Arrange
        Long userId = 1L;
        Long bookId = 1L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);

        // Act
        boolean result = favoriteService.isFavorited(userId, bookId);

        // Assert
        assertTrue(result);

        // Verify repository calls
        verify(favoriteRepository, times(1)).existsByUserIdAndBookId(userId, bookId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void isFavorited_WhenNotFavorited_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        Long bookId = 1L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);

        // Act
        boolean result = favoriteService.isFavorited(userId, bookId);

        // Assert
        assertFalse(result);

        // Verify repository calls
        verify(favoriteRepository, times(1)).existsByUserIdAndBookId(userId, bookId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void isFavorited_WithNonExistentUser_ShouldReturnFalse() {
        // Arrange
        Long userId = 999L;
        Long bookId = 1L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);

        // Act
        boolean result = favoriteService.isFavorited(userId, bookId);

        // Assert
        assertFalse(result);

        // Verify repository calls
        verify(favoriteRepository, times(1)).existsByUserIdAndBookId(userId, bookId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void isFavorited_WithNonExistentBook_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        Long bookId = 999L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);

        // Act
        boolean result = favoriteService.isFavorited(userId, bookId);

        // Assert
        assertFalse(result);

        // Verify repository calls
        verify(favoriteRepository, times(1)).existsByUserIdAndBookId(userId, bookId);
        verifyNoMoreInteractions(favoriteRepository, userRepository, bookRepository);
    }

    @Test
    void addFavorite_ShouldCreateFavoriteWithCorrectUserAndBook() {
        // Arrange
        Long userId = 1L;
        Long bookId = 1L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(invocation -> {
            Favorite favorite = invocation.getArgument(0);
            favorite.setId(1L);
            favorite.setCreatedAt(LocalDateTime.now());
            return favorite;
        });

        // Act
        FavoriteDTO result = favoriteService.addFavorite(userId, bookId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(bookId, result.getBookId());

        // Verify that the favorite was created with correct user and book
        verify(favoriteRepository).save(argThat(favorite -> 
            favorite.getUser().getId().equals(userId) && 
            favorite.getBook().getId().equals(bookId)
        ));
    }

    @Test
    void getFavorites_WithMultipleFavorites_ShouldReturnCorrectOrder() {
        // Arrange
        Long userId = 1L;
        List<Favorite> favorites = Arrays.asList(testFavorite, testFavorite2);
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(favorites);

        // Act
        List<FavoriteDTO> result = favoriteService.getFavorites(userId);

        // Assert
        assertEquals(2, result.size());
        assertEquals(testFavorite.getId(), result.get(0).getId());
        assertEquals(testFavorite2.getId(), result.get(1).getId());
    }

    @Test
    void addFavorite_WithNullUserId_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long userId = null;
        Long bookId = 1L;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> favoriteService.addFavorite(userId, bookId));
    }

    @Test
    void addFavorite_WithNullBookId_ShouldThrowIllegalArgumentException() {
        // Arrange
        Long userId = 1L;
        Long bookId = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> favoriteService.addFavorite(userId, bookId));
    }

    @Test
    void removeFavorite_WithNullUserId_ShouldNotThrowException() {
        // Arrange
        Long userId = null;
        Long bookId = 1L;

        // Act & Assert
        assertDoesNotThrow(() -> favoriteService.removeFavorite(userId, bookId));
    }

    @Test
    void removeFavorite_WithNullBookId_ShouldNotThrowException() {
        // Arrange
        Long userId = 1L;
        Long bookId = null;

        // Act & Assert
        assertDoesNotThrow(() -> favoriteService.removeFavorite(userId, bookId));
    }

    @Test
    void getFavorites_WithNullUserId_ShouldReturnEmptyList() {
        // Arrange
        Long userId = null;
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(Collections.emptyList());

        // Act
        List<FavoriteDTO> result = favoriteService.getFavorites(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void isFavorited_WithNullUserId_ShouldReturnFalse() {
        // Arrange
        Long userId = null;
        Long bookId = 1L;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);

        // Act
        boolean result = favoriteService.isFavorited(userId, bookId);

        // Assert
        assertFalse(result);
    }

    @Test
    void isFavorited_WithNullBookId_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        Long bookId = null;
        when(favoriteRepository.existsByUserIdAndBookId(userId, bookId)).thenReturn(false);

        // Act
        boolean result = favoriteService.isFavorited(userId, bookId);

        // Assert
        assertFalse(result);
    }
}
