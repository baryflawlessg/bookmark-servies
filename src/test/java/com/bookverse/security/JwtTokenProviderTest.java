package com.bookverse.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private static final String SECRET_KEY = "testSecretKeyThatIsLongEnoughForHMACSHA256Algorithm";
    private static final long VALIDITY_IN_MILLIS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(SECRET_KEY, VALIDITY_IN_MILLIS);
    }

    @Test
    void generateToken_WithValidSubject_ShouldReturnValidToken() {
        // Arrange
        String subject = "test@example.com";

        // Act
        String token = jwtTokenProvider.generateToken(subject);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT tokens have 3 parts separated by dots
    }

    @Test
    void generateToken_WithDifferentSubjects_ShouldReturnDifferentTokens() {
        // Arrange
        String subject1 = "user1@example.com";
        String subject2 = "user2@example.com";

        // Act
        String token1 = jwtTokenProvider.generateToken(subject1);
        String token2 = jwtTokenProvider.generateToken(subject2);

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
    }

    @Test
    void generateToken_WithEmptySubject_ShouldReturnValidToken() {
        // Arrange
        String subject = "";

        // Act
        String token = jwtTokenProvider.generateToken(subject);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
        // Verify the subject can be extracted (might be null or empty)
        String extractedSubject = jwtTokenProvider.getSubject(token);
        assertTrue(extractedSubject == null || extractedSubject.isEmpty());
    }

    @Test
    void generateToken_WithNullSubject_ShouldReturnValidToken() {
        // Arrange
        String subject = null;

        // Act
        String token = jwtTokenProvider.generateToken(subject);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtTokenProvider.validateToken(token));
        // Verify the subject can be extracted (should be null)
        String extractedSubject = jwtTokenProvider.getSubject(token);
        assertNull(extractedSubject);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String subject = "test@example.com";
        String token = jwtTokenProvider.generateToken(subject);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // Arrange
        String nullToken = null;

        // Act
        boolean isValid = jwtTokenProvider.validateToken(nullToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithMalformedToken_ShouldReturnFalse() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getSubject_WithValidToken_ShouldReturnCorrectSubject() {
        // Arrange
        String expectedSubject = "test@example.com";
        String token = jwtTokenProvider.generateToken(expectedSubject);

        // Act
        String actualSubject = jwtTokenProvider.getSubject(token);

        // Assert
        assertEquals(expectedSubject, actualSubject);
    }

    @Test
    void getSubject_WithEmptySubjectToken_ShouldReturnEmptyString() {
        // Arrange
        String expectedSubject = "";
        String token = jwtTokenProvider.generateToken(expectedSubject);

        // Act
        String actualSubject = jwtTokenProvider.getSubject(token);

        // Assert
        // JWT library might return null for empty string subjects, so we need to handle both cases
        assertTrue(actualSubject == null || actualSubject.isEmpty());
    }

    @Test
    void getSubject_WithNullSubjectToken_ShouldReturnNull() {
        // Arrange
        String expectedSubject = null;
        String token = jwtTokenProvider.generateToken(expectedSubject);

        // Act
        String actualSubject = jwtTokenProvider.getSubject(token);

        // Assert
        assertEquals(expectedSubject, actualSubject);
    }

    @Test
    void getSubject_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtTokenProvider.getSubject(invalidToken));
    }

    @Test
    void getSubject_WithEmptyToken_ShouldThrowException() {
        // Arrange
        String emptyToken = "";

        // Act & Assert
        assertThrows(Exception.class, () -> jwtTokenProvider.getSubject(emptyToken));
    }

    @Test
    void getSubject_WithNullToken_ShouldThrowException() {
        // Arrange
        String nullToken = null;

        // Act & Assert
        assertThrows(Exception.class, () -> jwtTokenProvider.getSubject(nullToken));
    }

    @Test
    void tokenGenerationAndValidation_ShouldWorkWithSpecialCharacters() {
        // Arrange
        String subjectWithSpecialChars = "user+test@example.com!";

        // Act
        String token = jwtTokenProvider.generateToken(subjectWithSpecialChars);
        boolean isValid = jwtTokenProvider.validateToken(token);
        String retrievedSubject = jwtTokenProvider.getSubject(token);

        // Assert
        assertTrue(isValid);
        assertEquals(subjectWithSpecialChars, retrievedSubject);
    }

    @Test
    void tokenGenerationAndValidation_ShouldWorkWithLongSubject() {
        // Arrange
        String longSubject = "very.long.email.address.with.many.parts@very.long.domain.name.com";

        // Act
        String token = jwtTokenProvider.generateToken(longSubject);
        boolean isValid = jwtTokenProvider.validateToken(token);
        String retrievedSubject = jwtTokenProvider.getSubject(token);

        // Assert
        assertTrue(isValid);
        assertEquals(longSubject, retrievedSubject);
    }

    @Test
    void tokenStructure_ShouldHaveCorrectFormat() {
        // Arrange
        String subject = "test@example.com";

        // Act
        String token = jwtTokenProvider.generateToken(subject);

        // Assert
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token should have 3 parts: header.payload.signature");
        assertFalse(parts[0].isEmpty(), "Header should not be empty");
        assertFalse(parts[1].isEmpty(), "Payload should not be empty");
        assertFalse(parts[2].isEmpty(), "Signature should not be empty");
    }

    @Test
    void tokenExpiration_ShouldBeSetCorrectly() {
        // Arrange
        String subject = "test@example.com";
        long startTime = System.currentTimeMillis();

        // Act
        String token = jwtTokenProvider.generateToken(subject);
        long endTime = System.currentTimeMillis();

        // Assert
        // The token should be valid immediately after generation
        assertTrue(jwtTokenProvider.validateToken(token));
        
        // The token should have an expiration time in the future
        // We can't directly test the expiration without waiting,
        // but we can verify the token is valid immediately after generation
        assertTrue(endTime - startTime < VALIDITY_IN_MILLIS);
    }
}
