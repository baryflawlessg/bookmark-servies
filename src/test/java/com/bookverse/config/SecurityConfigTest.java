package com.bookverse.config;

import com.bookverse.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private CorsConfigurationSource corsConfigurationSource;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtAuthenticationFilter, corsConfigurationSource);
    }

    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void passwordEncoder_ShouldEncodePasswordCorrectly() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";

        // Act
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Assert
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$"));
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void passwordEncoder_ShouldMatchEncodedPassword() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Act
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);

        // Assert
        assertTrue(matches);
    }

    @Test
    void passwordEncoder_ShouldNotMatchWrongPassword() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String correctPassword = "testPassword123";
        String wrongPassword = "wrongPassword";
        String encodedPassword = passwordEncoder.encode(correctPassword);

        // Act
        boolean matches = passwordEncoder.matches(wrongPassword, encodedPassword);

        // Assert
        assertFalse(matches);
    }

    @Test
    void passwordEncoder_ShouldGenerateDifferentEncodingsForSamePassword() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";

        // Act
        String encodedPassword1 = passwordEncoder.encode(rawPassword);
        String encodedPassword2 = passwordEncoder.encode(rawPassword);

        // Assert
        assertNotEquals(encodedPassword1, encodedPassword2);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword1));
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword2));
    }

    @Test
    void securityConfig_ShouldHaveRequiredDependencies() {
        // Assert
        assertNotNull(securityConfig);
        assertNotNull(jwtAuthenticationFilter);
        assertNotNull(corsConfigurationSource);
    }

    @Test
    void securityConfig_ShouldBeProperlyConfigured() {
        // Assert
        assertNotNull(securityConfig);
        // The SecurityConfig should be properly instantiated with required dependencies
    }

    @Test
    void passwordEncoder_ShouldHandleNullPassword() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> passwordEncoder.encode(null));
    }

    @Test
    void passwordEncoder_ShouldHandleEmptyPassword() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String emptyPassword = "";

        // Act
        String encodedPassword = passwordEncoder.encode(emptyPassword);

        // Assert
        assertNotNull(encodedPassword);
        assertTrue(passwordEncoder.matches(emptyPassword, encodedPassword));
    }

    @Test
    void passwordEncoder_ShouldHandleSpecialCharacters() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String passwordWithSpecialChars = "p@ssw0rd!@#$%^&*()";

        // Act
        String encodedPassword = passwordEncoder.encode(passwordWithSpecialChars);

        // Assert
        assertNotNull(encodedPassword);
        assertTrue(passwordEncoder.matches(passwordWithSpecialChars, encodedPassword));
    }

    @Test
    void passwordEncoder_ShouldHandleLongPassword() {
        // Arrange
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String longPassword = "a".repeat(1000);

        // Act
        String encodedPassword = passwordEncoder.encode(longPassword);

        // Assert
        assertNotNull(encodedPassword);
        assertTrue(passwordEncoder.matches(longPassword, encodedPassword));
    }
}
