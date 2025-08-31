package com.bookverse.controller;

import com.bookverse.dto.*;
import com.bookverse.security.JwtTokenProvider;
import com.bookverse.service.TokenBlacklistService;
import com.bookverse.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthController authController;

    @Test
    void signup_ShouldReturnSuccessResponse() {
        // Arrange
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO();
        registrationDTO.setName("John Doe");
        registrationDTO.setEmail("john@example.com");
        registrationDTO.setPassword("password123");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");

        when(userService.register(any(UserRegistrationDTO.class))).thenReturn(userDTO);
        when(tokenProvider.generateToken(anyString())).thenReturn("jwt-token");

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.signup(registrationDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("jwt-token", response.getBody().getData().getToken());
        verify(userService).register(registrationDTO);
        verify(tokenProvider).generateToken("john@example.com");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnSuccessResponse() {
        // Arrange
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("john@example.com");
        loginDTO.setPassword("password123");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");

        when(userService.login(any(UserLoginDTO.class))).thenReturn(Optional.of(userDTO));
        when(tokenProvider.generateToken(anyString())).thenReturn("jwt-token");

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("jwt-token", response.getBody().getData().getToken());
        verify(userService).login(loginDTO);
        verify(tokenProvider).generateToken("john@example.com");
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnErrorResponse() {
        // Arrange
        UserLoginDTO loginDTO = new UserLoginDTO();
        loginDTO.setEmail("john@example.com");
        loginDTO.setPassword("wrongpassword");

        when(userService.login(any(UserLoginDTO.class))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(loginDTO);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Invalid credentials", response.getBody().getMessage());
        verify(userService).login(loginDTO);
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void logout_WithValidBearerToken_ShouldBlacklistTokenAndReturnSuccess() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer jwt-token");

        // Act
        ResponseEntity<ApiResponse<String>> response = authController.logout(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Logged out successfully", response.getBody().getData());
        verify(tokenBlacklistService).blacklistToken("jwt-token");
    }

    @Test
    void logout_WithNoAuthorizationHeader_ShouldReturnSuccessWithoutBlacklisting() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        ResponseEntity<ApiResponse<String>> response = authController.logout(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Logged out successfully", response.getBody().getData());
        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void logout_WithAuthorizationHeaderWithoutBearer_ShouldReturnSuccessWithoutBlacklisting() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("jwt-token");

        // Act
        ResponseEntity<ApiResponse<String>> response = authController.logout(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Logged out successfully", response.getBody().getData());
        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void logout_WithEmptyAuthorizationHeader_ShouldReturnSuccessWithoutBlacklisting() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("");

        // Act
        ResponseEntity<ApiResponse<String>> response = authController.logout(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Logged out successfully", response.getBody().getData());
        verifyNoInteractions(tokenBlacklistService);
    }

    @Test
    void logout_WithBearerOnly_ShouldReturnSuccessWithoutBlacklisting() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        ResponseEntity<ApiResponse<String>> response = authController.logout(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Logged out successfully", response.getBody().getData());
        verify(tokenBlacklistService).blacklistToken("");
    }

    @Test
    void getCurrentUser_WithAuthenticatedUser_ShouldReturnUserProfile() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("John Doe");
        userDTO.setEmail("john@example.com");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userService.getCurrentUserProfile()).thenReturn(Optional.of(userDTO));
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
        assertEquals(userDTO, response.getBody().getData());
        verify(userService).getCurrentUserProfile();
    }

    @Test
    void getCurrentUser_WithNonAuthenticatedUser_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verifyNoInteractions(userService);
    }

    @Test
    void getCurrentUser_WithNullAuthentication_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verifyNoInteractions(userService);
    }

    @Test
    void getCurrentUser_WithAnonymousUser_ShouldReturnUnauthorized() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("Not authenticated", response.getBody().getMessage());
        verifyNoInteractions(userService);
    }

    @Test
    void getCurrentUser_WithUserNotFound_ShouldReturnNotFound() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");
        when(userService.getCurrentUserProfile()).thenReturn(Optional.empty());
        SecurityContextHolder.setContext(securityContext);

        // Act
        ResponseEntity<ApiResponse<UserDTO>> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().isSuccess());
        assertEquals("User not found", response.getBody().getMessage());
        verify(userService).getCurrentUserProfile();
    }
}
