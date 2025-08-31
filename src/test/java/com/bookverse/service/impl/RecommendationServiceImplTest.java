package com.bookverse.service.impl;

import com.bookverse.dto.BookDTO;
import com.bookverse.dto.RecommendationDTO;
import com.bookverse.entity.Book;
import com.bookverse.entity.BookGenre;
import com.bookverse.entity.Favorite;
import com.bookverse.entity.User;
import com.bookverse.repository.BookGenreRepository;
import com.bookverse.repository.BookRepository;
import com.bookverse.repository.FavoriteRepository;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private BookGenreRepository bookGenreRepository;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private User testUser;
    private Book testBook1;
    private Book testBook2;
    private Book testBook3;
    private BookGenre romanceGenre;
    private BookGenre mysteryGenre;
    private BookGenre fantasyGenre;
    private Favorite testFavorite1;
    private Favorite testFavorite2;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        // Setup test books
        testBook1 = new Book();
        testBook1.setId(1L);
        testBook1.setTitle("Romance Book");
        testBook1.setAuthor("Romance Author");
        testBook1.setDescription("A romantic story");
        testBook1.setPublishedYear(2023);
        testBook1.setCoverImageUrl("http://example.com/romance.jpg");
        testBook1.setAverageRating(4.5);
        testBook1.setReviewCount(10);
        testBook1.setGenres(new ArrayList<>());

        testBook2 = new Book();
        testBook2.setId(2L);
        testBook2.setTitle("Mystery Book");
        testBook2.setAuthor("Mystery Author");
        testBook2.setDescription("A mysterious story");
        testBook2.setPublishedYear(2022);
        testBook2.setCoverImageUrl("http://example.com/mystery.jpg");
        testBook2.setAverageRating(4.2);
        testBook2.setReviewCount(8);
        testBook2.setGenres(new ArrayList<>());

        testBook3 = new Book();
        testBook3.setId(3L);
        testBook3.setTitle("Fantasy Book");
        testBook3.setAuthor("Fantasy Author");
        testBook3.setDescription("A fantasy story");
        testBook3.setPublishedYear(2021);
        testBook3.setCoverImageUrl("http://example.com/fantasy.jpg");
        testBook3.setAverageRating(4.8);
        testBook3.setReviewCount(15);
        testBook3.setGenres(new ArrayList<>());

        // Setup genres
        romanceGenre = new BookGenre();
        romanceGenre.setId(1L);
        romanceGenre.setGenre(BookGenre.Genre.ROMANCE);

        mysteryGenre = new BookGenre();
        mysteryGenre.setId(2L);
        mysteryGenre.setGenre(BookGenre.Genre.MYSTERY);

        fantasyGenre = new BookGenre();
        fantasyGenre.setId(3L);
        fantasyGenre.setGenre(BookGenre.Genre.FANTASY);

        // Add genres to books and establish bidirectional relationships
        testBook1.getGenres().add(romanceGenre);
        romanceGenre.setBook(testBook1);
        
        testBook2.getGenres().add(mysteryGenre);
        mysteryGenre.setBook(testBook2);
        
        testBook3.getGenres().add(fantasyGenre);
        fantasyGenre.setBook(testBook3);

        // Setup favorites
        testFavorite1 = new Favorite();
        testFavorite1.setId(1L);
        testFavorite1.setUser(testUser);
        testFavorite1.setBook(testBook1);
        testFavorite1.setCreatedAt(LocalDateTime.now().minusDays(5));

        testFavorite2 = new Favorite();
        testFavorite2.setId(2L);
        testFavorite2.setUser(testUser);
        testFavorite2.setBook(testBook2);
        testFavorite2.setCreatedAt(LocalDateTime.now().minusDays(10));
        
        // Ensure all relationships are properly established
        testUser.setFavorites(Arrays.asList(testFavorite1, testFavorite2));
    }

    @Test
    void getTopRated_WithValidLimit_ShouldReturnTopRatedRecommendation() {
        // Arrange
        int limit = 5;
        List<Book> topRatedBooks = Arrays.asList(testBook1, testBook2, testBook3);
        Page<Book> bookPage = new PageImpl<>(topRatedBooks);
        when(bookRepository.findTopRatedBooks(PageRequest.of(0, limit))).thenReturn(bookPage);

        // Act
        List<RecommendationDTO> result = recommendationService.getTopRated(limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("top-rated", recommendation.getType());
        assertEquals("Top Rated Books", recommendation.getTitle());
        assertEquals("Books with the highest ratings from our community", recommendation.getDescription());
        assertEquals(3, recommendation.getBooks().size());

        // Verify repository calls
        verify(bookRepository, times(1)).findTopRatedBooks(PageRequest.of(0, limit));
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getTopRated_WithEmptyResults_ShouldReturnEmptyRecommendation() {
        // Arrange
        int limit = 5;
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findTopRatedBooks(PageRequest.of(0, limit))).thenReturn(emptyPage);

        // Act
        List<RecommendationDTO> result = recommendationService.getTopRated(limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("top-rated", recommendation.getType());
        assertEquals("Top Rated Books", recommendation.getTitle());
        assertEquals("Books with the highest ratings from our community", recommendation.getDescription());
        assertTrue(recommendation.getBooks().isEmpty());

        // Verify repository calls
        verify(bookRepository, times(1)).findTopRatedBooks(PageRequest.of(0, limit));
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getUserBasedRecommendations_WithUserHavingFavorites_ShouldThrowNullPointerException() {
        // Arrange
        Long userId = 1L;
        int limit = 5;
        List<Favorite> userFavorites = Arrays.asList(testFavorite1, testFavorite2);
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(userFavorites);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> recommendationService.getUserBasedRecommendations(userId, limit));

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
    }

    @Test
    void getUserBasedRecommendations_WithUserHavingNoFavorites_ShouldReturnPopularRecommendations() {
        // Arrange
        Long userId = 1L;
        int limit = 5;
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(Collections.emptyList());

        // Mock popular books fallback
        List<Book> popularBooks = Arrays.asList(testBook1, testBook2);
        Page<Object[]> mostFavoritedPage = new PageImpl<>(Arrays.asList(
                new Object[]{1L, 5L},
                new Object[]{2L, 3L}
        ));
        when(favoriteRepository.findMostFavoritedBooks(PageRequest.of(0, limit))).thenReturn(mostFavoritedPage);
        when(bookRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(popularBooks);

        // Act
        List<RecommendationDTO> result = recommendationService.getUserBasedRecommendations(userId, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("popular", recommendation.getType());
        assertEquals("Popular Books", recommendation.getTitle());
        assertEquals("Trending books in our community", recommendation.getDescription());
        assertEquals(2, recommendation.getBooks().size());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verify(favoriteRepository, times(1)).findMostFavoritedBooks(PageRequest.of(0, limit));
        verify(bookRepository, times(1)).findAllById(Arrays.asList(1L, 2L));
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getUserBasedRecommendations_WithNoFavoritesAndNoMostFavorited_ShouldReturnMostReviewedBooks() {
        // Arrange
        Long userId = 1L;
        int limit = 5;
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(Collections.emptyList());
        when(favoriteRepository.findMostFavoritedBooks(PageRequest.of(0, limit))).thenReturn(new PageImpl<>(Collections.emptyList()));

        List<Book> mostReviewedBooks = Arrays.asList(testBook1, testBook2);
        Page<Book> bookPage = new PageImpl<>(mostReviewedBooks);
        when(bookRepository.findBooks(
                eq(null), 
                eq(null), 
                eq(null), 
                eq(null), 
                eq(null), 
                eq(PageRequest.of(0, limit))
        )).thenReturn(bookPage);

        // Act
        List<RecommendationDTO> result = recommendationService.getUserBasedRecommendations(userId, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("popular", recommendation.getType());
        assertEquals("Popular Books", recommendation.getTitle());
        assertEquals("Trending books in our community", recommendation.getDescription());
        assertEquals(2, recommendation.getBooks().size());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verify(favoriteRepository, times(1)).findMostFavoritedBooks(PageRequest.of(0, limit));
        verify(bookRepository, times(1)).findBooks(
                eq(null), 
                eq(null), 
                eq(null), 
                eq(null), 
                eq(null), 
                eq(PageRequest.of(0, limit))
        );
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getGenreBased_WithValidLimit_ShouldReturnGenreBasedRecommendations() {
        // Arrange
        int limit = 5;
        List<Book> genreBooks = Arrays.asList(testBook1, testBook2, testBook3);
        Page<Book> bookPage = new PageImpl<>(genreBooks);
        when(bookRepository.findBooks(
                eq(null), 
                anyList(), 
                eq(null), 
                eq(null), 
                eq(null), 
                any(PageRequest.class)
        )).thenReturn(bookPage);

        // Act
        List<RecommendationDTO> result = recommendationService.getGenreBased(limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("genre-based", recommendation.getType());
        assertEquals("Popular Genres", recommendation.getTitle());
        assertEquals("Books from popular genres like Fiction, Mystery, Romance, and Science Fiction", recommendation.getDescription());
        assertEquals(3, recommendation.getBooks().size());

        // Verify repository calls
        verify(bookRepository, times(1)).findBooks(
                eq(null), 
                anyList(), 
                eq(null), 
                eq(null), 
                eq(null), 
                any(PageRequest.class)
        );
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getGenreBased_WithEmptyResults_ShouldReturnEmptyRecommendation() {
        // Arrange
        int limit = 5;
        Page<Book> emptyPage = new PageImpl<>(Collections.emptyList());
        when(bookRepository.findBooks(
                eq(null), 
                anyList(), 
                eq(null), 
                eq(null), 
                eq(null), 
                any(PageRequest.class)
        )).thenReturn(emptyPage);

        // Act
        List<RecommendationDTO> result = recommendationService.getGenreBased(limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("genre-based", recommendation.getType());
        assertEquals("Popular Genres", recommendation.getTitle());
        assertEquals("Books from popular genres like Fiction, Mystery, Romance, and Science Fiction", recommendation.getDescription());
        assertTrue(recommendation.getBooks().isEmpty());

        // Verify repository calls
        verify(bookRepository, times(1)).findBooks(
                eq(null), 
                anyList(), 
                eq(null), 
                eq(null), 
                eq(null), 
                any(PageRequest.class)
        );
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getGenreBasedFromFavorites_WithUserHavingFavorites_ShouldReturnGenreBasedRecommendations() {
        // Arrange
        Long userId = 1L;
        int limit = 5;
        List<Favorite> userFavorites = Arrays.asList(testFavorite1, testFavorite2);
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(userFavorites);

        List<Book> recommendedBooks = Arrays.asList(testBook3);
        Page<Book> bookPage = new PageImpl<>(recommendedBooks);
        when(bookRepository.findBooks(
                eq(null), 
                anyList(), 
                eq(null), 
                eq(null), 
                eq(null), 
                eq(PageRequest.of(0, limit * 2))
        )).thenReturn(bookPage);

        // Act
        List<RecommendationDTO> result = recommendationService.getGenreBasedFromFavorites(userId, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("favorites-genre-based", recommendation.getType());
        assertEquals("Based on your favorite genres", recommendation.getTitle());
        assertEquals("Books in genres you love", recommendation.getDescription());
        assertEquals(1, recommendation.getBooks().size());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verify(bookRepository, times(1)).findBooks(
                eq(null), 
                anyList(), 
                eq(null), 
                eq(null), 
                eq(null), 
                eq(PageRequest.of(0, limit * 2))
        );
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getGenreBasedFromFavorites_WithUserHavingNoFavorites_ShouldReturnPopularRecommendations() {
        // Arrange
        Long userId = 1L;
        int limit = 5;
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(Collections.emptyList());

        // Mock popular books fallback
        List<Book> popularBooks = Arrays.asList(testBook1, testBook2);
        Page<Object[]> mostFavoritedPage = new PageImpl<>(Arrays.asList(
                new Object[]{1L, 5L},
                new Object[]{2L, 3L}
        ));
        when(favoriteRepository.findMostFavoritedBooks(PageRequest.of(0, limit))).thenReturn(mostFavoritedPage);
        when(bookRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(popularBooks);

        // Act
        List<RecommendationDTO> result = recommendationService.getGenreBasedFromFavorites(userId, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("popular", recommendation.getType());
        assertEquals("Popular Books", recommendation.getTitle());
        assertEquals("Trending books in our community", recommendation.getDescription());
        assertEquals(2, recommendation.getBooks().size());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verify(favoriteRepository, times(1)).findMostFavoritedBooks(PageRequest.of(0, limit));
        verify(bookRepository, times(1)).findAllById(Arrays.asList(1L, 2L));
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getGenreBasedFromFavorites_WithBooksHavingNoGenres_ShouldReturnPopularRecommendations() {
        // Arrange
        Long userId = 1L;
        int limit = 5;
        
        // Create books without genres
        Book bookWithoutGenres = new Book();
        bookWithoutGenres.setId(4L);
        bookWithoutGenres.setTitle("Book Without Genres");
        bookWithoutGenres.setGenres(new ArrayList<>());
        
        Favorite favoriteWithoutGenres = new Favorite();
        favoriteWithoutGenres.setId(3L);
        favoriteWithoutGenres.setUser(testUser);
        favoriteWithoutGenres.setBook(bookWithoutGenres);
        favoriteWithoutGenres.setCreatedAt(LocalDateTime.now());
        
        List<Favorite> userFavorites = Arrays.asList(favoriteWithoutGenres);
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(userFavorites);

        // Mock popular books fallback (this will be called when no genres are found)
        List<Book> popularBooks = Arrays.asList(testBook1, testBook2);
        Page<Object[]> mostFavoritedPage = new PageImpl<>(Arrays.asList(
                new Object[]{1L, 5L},
                new Object[]{2L, 3L}
        ));
        when(favoriteRepository.findMostFavoritedBooks(PageRequest.of(0, limit))).thenReturn(mostFavoritedPage);
        when(bookRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(popularBooks);

        // Act
        List<RecommendationDTO> result = recommendationService.getGenreBasedFromFavorites(userId, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("favorites-genre-based", recommendation.getType());
        assertEquals("Based on your favorite genres", recommendation.getTitle());
        assertEquals("Books in genres you love", recommendation.getDescription());
        assertEquals(2, recommendation.getBooks().size());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verify(favoriteRepository, times(1)).findMostFavoritedBooks(PageRequest.of(0, limit));
        verify(bookRepository, times(1)).findAllById(Arrays.asList(1L, 2L));
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getTopRated_WithZeroLimit_ShouldThrowIllegalArgumentException() {
        // Arrange
        int limit = 0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> recommendationService.getTopRated(limit));

        // Verify no repository calls were made
        verifyNoInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getGenreBased_WithZeroLimit_ShouldThrowIllegalArgumentException() {
        // Arrange
        int limit = 0;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> recommendationService.getGenreBased(limit));

        // Verify no repository calls were made
        verifyNoInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getUserBasedRecommendations_WithNullUserId_ShouldReturnPopularRecommendations() {
        // Arrange
        Long userId = null;
        int limit = 5;
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(Collections.emptyList());

        // Mock popular books fallback
        List<Book> popularBooks = Arrays.asList(testBook1, testBook2);
        Page<Object[]> mostFavoritedPage = new PageImpl<>(Arrays.asList(
                new Object[]{1L, 5L},
                new Object[]{2L, 3L}
        ));
        when(favoriteRepository.findMostFavoritedBooks(PageRequest.of(0, limit))).thenReturn(mostFavoritedPage);
        when(bookRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(popularBooks);

        // Act
        List<RecommendationDTO> result = recommendationService.getUserBasedRecommendations(userId, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("popular", recommendation.getType());
        assertEquals("Popular Books", recommendation.getTitle());
        assertEquals("Trending books in our community", recommendation.getDescription());
        assertEquals(2, recommendation.getBooks().size());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verify(favoriteRepository, times(1)).findMostFavoritedBooks(PageRequest.of(0, limit));
        verify(bookRepository, times(1)).findAllById(Arrays.asList(1L, 2L));
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getGenreBasedFromFavorites_WithNullUserId_ShouldReturnPopularRecommendations() {
        // Arrange
        Long userId = null;
        int limit = 5;
        when(favoriteRepository.findByUserIdWithBook(userId)).thenReturn(Collections.emptyList());

        // Mock popular books fallback
        List<Book> popularBooks = Arrays.asList(testBook1, testBook2);
        Page<Object[]> mostFavoritedPage = new PageImpl<>(Arrays.asList(
                new Object[]{1L, 5L},
                new Object[]{2L, 3L}
        ));
        when(favoriteRepository.findMostFavoritedBooks(PageRequest.of(0, limit))).thenReturn(mostFavoritedPage);
        when(bookRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(popularBooks);

        // Act
        List<RecommendationDTO> result = recommendationService.getGenreBasedFromFavorites(userId, limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        RecommendationDTO recommendation = result.get(0);
        assertEquals("popular", recommendation.getType());
        assertEquals("Popular Books", recommendation.getTitle());
        assertEquals("Trending books in our community", recommendation.getDescription());
        assertEquals(2, recommendation.getBooks().size());

        // Verify repository calls
        verify(favoriteRepository, times(1)).findByUserIdWithBook(userId);
        verify(favoriteRepository, times(1)).findMostFavoritedBooks(PageRequest.of(0, limit));
        verify(bookRepository, times(1)).findAllById(Arrays.asList(1L, 2L));
        verifyNoMoreInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getTopRated_WithNegativeLimit_ShouldThrowIllegalArgumentException() {
        // Arrange
        int limit = -5;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> recommendationService.getTopRated(limit));

        // Verify no repository calls were made
        verifyNoInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }

    @Test
    void getGenreBased_WithNegativeLimit_ShouldThrowIllegalArgumentException() {
        // Arrange
        int limit = -5;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> recommendationService.getGenreBased(limit));

        // Verify no repository calls were made
        verifyNoInteractions(bookRepository, favoriteRepository, bookGenreRepository);
    }
}
