package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.service.FavoriteService;
import com.bookverse.service.ReviewService;
import com.bookverse.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private FavoriteService favoriteService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    @Test
    void getUser_ShouldReturnUser() {
        // Arrange
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("John Doe");
        when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(user, response.getBody().getData());
        verify(userService).getUserById(1L);
    }

    @Test
    void getUser_WithNonExistentUser_ShouldReturnNotFound() {
        // Arrange
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getUser(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).getUserById(999L);
    }

    @Test
    void getCurrentUserProfile_WithAuthenticatedUser_ShouldReturnProfile() {
        // Arrange
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("John Doe");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userService.getCurrentUserProfile()).thenReturn(Optional.of(user));
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getCurrentUserProfile();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(user, response.getBody().getData());
        verify(userService).getCurrentUserProfile();
    }

    @Test
    void getCurrentUserProfile_WithNonAuthenticatedUser_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getCurrentUserProfile();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).getCurrentUserProfile();
    }

    @Test
    void getCurrentUserProfile_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getCurrentUserProfile();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).getCurrentUserProfile();
    }

    @Test
    void getCurrentUserProfile_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getCurrentUserProfile();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).getCurrentUserProfile();
    }

    @Test
    void getCurrentUserProfile_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userService.getCurrentUserProfile()).thenReturn(Optional.empty());
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getCurrentUserProfile();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getMessage());
        verify(userService).getCurrentUserProfile();
    }

    @Test
    void updateProfile_WithAuthenticatedUser_ShouldReturnUpdatedProfile() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("John Smith");
        updateDTO.setEmail("john.smith@example.com");

        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setName("John Smith");
        user.setEmail("john.smith@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userService.updateProfile("john@example.com", updateDTO)).thenReturn(Optional.of(user));
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.updateProfile(updateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(user, response.getBody().getData());
        verify(userService).updateProfile("john@example.com", updateDTO);
    }

    @Test
    void updateProfile_WithNonAuthenticatedUser_ShouldReturnUnauthorized() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("John Smith");
        updateDTO.setEmail("john.smith@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.updateProfile(updateDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).updateProfile(anyString(), any(UserUpdateDTO.class));
    }

    @Test
    void updateProfile_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("John Smith");
        updateDTO.setEmail("john.smith@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.updateProfile(updateDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).updateProfile(anyString(), any(UserUpdateDTO.class));
    }

    @Test
    void updateProfile_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("John Smith");
        updateDTO.setEmail("john.smith@example.com");

        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.updateProfile(updateDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).updateProfile(anyString(), any(UserUpdateDTO.class));
    }

    @Test
    void updateProfile_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("John Smith");
        updateDTO.setEmail("john.smith@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userService.updateProfile("john@example.com", updateDTO)).thenReturn(Optional.empty());
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = userController.updateProfile(updateDTO);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getMessage());
        verify(userService).updateProfile("john@example.com", updateDTO);
    }

    @Test
    void deleteAccount_WithAuthenticatedUser_ShouldReturnSuccess() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userService.deleteAccount("john@example.com")).thenReturn(true);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<String>> response = userController.deleteAccount();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Account deleted successfully", response.getBody().getData());
        verify(userService).deleteAccount("john@example.com");
    }

    @Test
    void deleteAccount_WithNonAuthenticatedUser_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<String>> response = userController.deleteAccount();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).deleteAccount(anyString());
    }

    @Test
    void deleteAccount_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<String>> response = userController.deleteAccount();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).deleteAccount(anyString());
    }

    @Test
    void deleteAccount_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<String>> response = userController.deleteAccount();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verify(userService, never()).deleteAccount(anyString());
    }

    @Test
    void deleteAccount_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userService.deleteAccount("john@example.com")).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<String>> response = userController.deleteAccount();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getMessage());
        verify(userService).deleteAccount("john@example.com");
    }

    @Test
    void getUserReviews_ShouldReturnReviews() {
        // Arrange
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setBookTitle("Test Book");
        PaginationDTO pagination = PaginationDTO.builder().page(0).size(20).totalElements(1).totalPages(1).first(true).last(false).build();
        PageResponse<ReviewDTO> pageResponse = PageResponse.of(Arrays.asList(review), pagination);
        when(reviewService.getReviewsForUser(1L, 0, 20)).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> response = userController.getUserReviews(1L, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getItems().size());
        verify(reviewService).getReviewsForUser(1L, 0, 20);
    }

    @Test
    void getUserReviews_WithDefaultParameters_ShouldUseDefaults() {
        // Arrange
        ReviewDTO review = new ReviewDTO();
        review.setId(1L);
        review.setBookTitle("Test Book");
        PaginationDTO pagination = PaginationDTO.builder().page(0).size(20).totalElements(1).totalPages(1).first(true).last(false).build();
        PageResponse<ReviewDTO> pageResponse = PageResponse.of(Arrays.asList(review), pagination);
        when(reviewService.getReviewsForUser(1L, 0, 20)).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponse<ReviewDTO>>> response = userController.getUserReviews(1L, 0, 20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().getItems().size());
        verify(reviewService).getReviewsForUser(1L, 0, 20);
    }

    @Test
    void getUserFavorites_ShouldReturnFavorites() {
        // Arrange
        FavoriteDTO favorite = new FavoriteDTO();
        favorite.setId(1L);
        favorite.setBookTitle("Test Book");
        List<FavoriteDTO> favorites = Arrays.asList(favorite);
        when(favoriteService.getFavorites(1L)).thenReturn(favorites);

        // Act
        ResponseEntity<ApiResponse<List<FavoriteDTO>>> response = userController.getUserFavorites(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1, response.getBody().getData().size());
        verify(favoriteService).getFavorites(1L);
    }

    @Test
    void getUserFavorites_WithEmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(favoriteService.getFavorites(1L)).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<ApiResponse<List<FavoriteDTO>>> response = userController.getUserFavorites(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().isEmpty());
        verify(favoriteService).getFavorites(1L);
    }

    @Test
    void addFavorite_ShouldReturnFavorite() {
        // Arrange
        FavoriteDTO favorite = new FavoriteDTO();
        favorite.setId(1L);
        favorite.setBookTitle("Test Book");
        when(favoriteService.addFavorite(1L, 1L)).thenReturn(favorite);

        // Act
        ResponseEntity<ApiResponse<FavoriteDTO>> response = userController.addFavorite(1L, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(favorite, response.getBody().getData());
        verify(favoriteService).addFavorite(1L, 1L);
    }

    @Test
    void removeFavorite_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = userController.removeFavorite(1L, 1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(favoriteService).removeFavorite(1L, 1L);
    }
}
