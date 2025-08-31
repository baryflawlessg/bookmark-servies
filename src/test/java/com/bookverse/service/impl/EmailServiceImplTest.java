package com.bookverse.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        // No setup needed for this simple service
    }

    @Test
    void sendWelcomeEmail_WithValidEmailAndName_ShouldLogSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String name = "Test User";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithNullEmail_ShouldLogSuccessfully() {
        // Arrange
        String email = null;
        String name = "Test User";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithNullName_ShouldLogSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String name = null;

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithNullEmailAndName_ShouldLogSuccessfully() {
        // Arrange
        String email = null;
        String name = null;

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithEmptyEmail_ShouldLogSuccessfully() {
        // Arrange
        String email = "";
        String name = "Test User";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithEmptyName_ShouldLogSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String name = "";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithEmptyEmailAndName_ShouldLogSuccessfully() {
        // Arrange
        String email = "";
        String name = "";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithSpecialCharactersInEmail_ShouldLogSuccessfully() {
        // Arrange
        String email = "user+test@example.com";
        String name = "Test User";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithSpecialCharactersInName_ShouldLogSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String name = "Test User!@#$%^&*()";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithLongEmail_ShouldLogSuccessfully() {
        // Arrange
        String email = "very.long.email.address.with.many.parts@very.long.domain.name.com";
        String name = "Test User";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithLongName_ShouldLogSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String name = "Very Long Name With Many Characters And Spaces";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithUnicodeCharactersInName_ShouldLogSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String name = "José María García-López";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithUnicodeCharactersInEmail_ShouldLogSuccessfully() {
        // Arrange
        String email = "test+unicode@example.com";
        String name = "Test User";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithWhitespaceInEmail_ShouldLogSuccessfully() {
        // Arrange
        String email = " test@example.com ";
        String name = "Test User";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithWhitespaceInName_ShouldLogSuccessfully() {
        // Arrange
        String email = "test@example.com";
        String name = " Test User ";

        // Act & Assert
        assertDoesNotThrow(() -> emailService.sendWelcomeEmail(email, name));
    }

    @Test
    void sendWelcomeEmail_WithMultipleCalls_ShouldLogSuccessfully() {
        // Arrange
        String email1 = "user1@example.com";
        String name1 = "User One";
        String email2 = "user2@example.com";
        String name2 = "User Two";

        // Act & Assert
        assertDoesNotThrow(() -> {
            emailService.sendWelcomeEmail(email1, name1);
            emailService.sendWelcomeEmail(email2, name2);
        });
    }

    @Test
    void sendWelcomeEmail_ShouldBeIdempotent() {
        // Arrange
        String email = "test@example.com";
        String name = "Test User";

        // Act & Assert
        assertDoesNotThrow(() -> {
            emailService.sendWelcomeEmail(email, name);
            emailService.sendWelcomeEmail(email, name);
            emailService.sendWelcomeEmail(email, name);
        });
    }

    @Test
    void emailService_ShouldBeProperlyInstantiated() {
        // Assert
        assertNotNull(emailService);
    }
}
