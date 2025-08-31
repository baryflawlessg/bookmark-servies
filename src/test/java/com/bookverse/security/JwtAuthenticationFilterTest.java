package com.bookverse.security;

import com.bookverse.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.jwt.token";
    private static final String USER_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + VALID_TOKEN);
        when(tokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(tokenProvider.getSubject(VALID_TOKEN)).thenReturn(USER_EMAIL);
        when(userDetailsService.loadUserByUsername(USER_EMAIL)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider).validateToken(VALID_TOKEN);
        verify(tokenBlacklistService).isBlacklisted(VALID_TOKEN);
        verify(tokenProvider).getSubject(VALID_TOKEN);
        verify(userDetailsService).loadUserByUsername(USER_EMAIL);
        verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + INVALID_TOKEN);
        when(tokenProvider.validateToken(INVALID_TOKEN)).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider).validateToken(INVALID_TOKEN);
        verify(tokenBlacklistService, never()).isBlacklisted(anyString());
        verify(tokenProvider, never()).getSubject(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithBlacklistedToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + VALID_TOKEN);
        when(tokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider).validateToken(VALID_TOKEN);
        verify(tokenBlacklistService).isBlacklisted(VALID_TOKEN);
        verify(tokenProvider, never()).getSubject(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoAuthorizationHeader_ShouldContinueChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider, never()).validateToken(anyString());
        verify(tokenBlacklistService, never()).isBlacklisted(anyString());
        verify(tokenProvider, never()).getSubject(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMalformedHeader_ShouldContinueChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidHeader");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider, never()).validateToken(anyString());
        verify(tokenBlacklistService, never()).isBlacklisted(anyString());
        verify(tokenProvider, never()).getSubject(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithEmptyBearerToken_ShouldProcessEmptyToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer ");
        when(tokenProvider.validateToken("")).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenProvider).validateToken("");
        verify(tokenBlacklistService, never()).isBlacklisted(anyString());
        verify(tokenProvider, never()).getSubject(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(securityContext, never()).setAuthentication(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithValidTokenButUserNotFound_ShouldThrowException() {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + VALID_TOKEN);
        when(tokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(tokenProvider.getSubject(VALID_TOKEN)).thenReturn(USER_EMAIL);
        when(userDetailsService.loadUserByUsername(USER_EMAIL))
                .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        // Act & Assert
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> {
            try {
                filter.doFilterInternal(request, response, filterChain);
            } catch (ServletException | IOException e) {
                throw new RuntimeException(e);
            }
        });

        verify(tokenProvider).validateToken(VALID_TOKEN);
        verify(tokenBlacklistService).isBlacklisted(VALID_TOKEN);
        verify(tokenProvider).getSubject(VALID_TOKEN);
        verify(userDetailsService).loadUserByUsername(USER_EMAIL);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void doFilterInternal_WithValidTokenAndUserDetails_ShouldSetCorrectAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + VALID_TOKEN);
        when(tokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(tokenBlacklistService.isBlacklisted(VALID_TOKEN)).thenReturn(false);
        when(tokenProvider.getSubject(VALID_TOKEN)).thenReturn(USER_EMAIL);
        when(userDetailsService.loadUserByUsername(USER_EMAIL)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(securityContext).setAuthentication(argThat(auth -> {
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) auth;
            return token.getPrincipal().equals(userDetails) &&
                   token.getCredentials() == null &&
                   token.getAuthorities().equals(userDetails.getAuthorities());
        }));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldAlwaysCallFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + INVALID_TOKEN);
        when(tokenProvider.validateToken(INVALID_TOKEN)).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }
}
