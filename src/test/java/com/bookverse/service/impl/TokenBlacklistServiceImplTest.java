package com.bookverse.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceImplTest {

    private TokenBlacklistServiceImpl tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistServiceImpl();
    }

    @Test
    void blacklistToken_WithValidToken_ShouldAddToBlacklist() {
        // Arrange
        String token = "valid.jwt.token";

        // Act
        tokenBlacklistService.blacklistToken(token);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void blacklistToken_WithEmptyToken_ShouldAddToBlacklist() {
        // Arrange
        String token = "";

        // Act
        tokenBlacklistService.blacklistToken(token);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void blacklistToken_WithNullToken_ShouldNotAddToBlacklist() {
        // Arrange
        String token = null;

        // Act
        tokenBlacklistService.blacklistToken(token);

        // Assert
        assertFalse(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void blacklistToken_WithMultipleTokens_ShouldAddAllToBlacklist() {
        // Arrange
        String token1 = "token1.jwt";
        String token2 = "token2.jwt";
        String token3 = "token3.jwt";

        // Act
        tokenBlacklistService.blacklistToken(token1);
        tokenBlacklistService.blacklistToken(token2);
        tokenBlacklistService.blacklistToken(token3);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(token1));
        assertTrue(tokenBlacklistService.isBlacklisted(token2));
        assertTrue(tokenBlacklistService.isBlacklisted(token3));
    }

    @Test
    void isBlacklisted_WithNonBlacklistedToken_ShouldReturnFalse() {
        // Arrange
        String token = "non.blacklisted.token";

        // Act
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertFalse(isBlacklisted);
    }

    @Test
    void isBlacklisted_WithBlacklistedToken_ShouldReturnTrue() {
        // Arrange
        String token = "blacklisted.token";
        tokenBlacklistService.blacklistToken(token);

        // Act
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertTrue(isBlacklisted);
    }

    @Test
    void isBlacklisted_WithEmptyToken_ShouldReturnFalse() {
        // Arrange
        String token = "";

        // Act
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertFalse(isBlacklisted);
    }

    @Test
    void isBlacklisted_WithNullToken_ShouldReturnFalse() {
        // Arrange
        String token = null;

        // Act
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertFalse(isBlacklisted);
    }

    @Test
    void blacklistAndCheck_WithSpecialCharacters_ShouldWorkCorrectly() {
        // Arrange
        String token = "special.token.with+chars@domain.com!";

        // Act
        tokenBlacklistService.blacklistToken(token);
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertTrue(isBlacklisted);
    }

    @Test
    void blacklistAndCheck_WithLongToken_ShouldWorkCorrectly() {
        // Arrange
        String token = "very.long.jwt.token.with.many.parts.and.characters.that.should.be.handled.correctly";

        // Act
        tokenBlacklistService.blacklistToken(token);
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertTrue(isBlacklisted);
    }

    @Test
    void blacklistAndCheck_WithDuplicateTokens_ShouldWorkCorrectly() {
        // Arrange
        String token = "duplicate.token";

        // Act
        tokenBlacklistService.blacklistToken(token);
        tokenBlacklistService.blacklistToken(token); // Duplicate call
        boolean isBlacklisted = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertTrue(isBlacklisted);
    }

    @Test
    void cleanupExpiredTokens_ShouldNotThrowException() {
        // Act & Assert
        assertDoesNotThrow(() -> tokenBlacklistService.cleanupExpiredTokens());
    }

    @Test
    void blacklistAndCheck_WithMixedTokenTypes_ShouldWorkCorrectly() {
        // Arrange
        String token1 = "normal.token";
        String token2 = "";
        String token3 = null;
        String token4 = "special.token!@#$%";

        // Act
        tokenBlacklistService.blacklistToken(token1);
        tokenBlacklistService.blacklistToken(token2);
        tokenBlacklistService.blacklistToken(token3);
        tokenBlacklistService.blacklistToken(token4);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(token1));
        assertTrue(tokenBlacklistService.isBlacklisted(token2));
        assertFalse(tokenBlacklistService.isBlacklisted(token3)); // null tokens are not blacklisted
        assertTrue(tokenBlacklistService.isBlacklisted(token4));
    }

    @Test
    void blacklistAndCheck_WithCaseSensitiveTokens_ShouldWorkCorrectly() {
        // Arrange
        String token1 = "TOKEN.UPPERCASE";
        String token2 = "token.lowercase";
        String token3 = "Token.MixedCase";

        // Act
        tokenBlacklistService.blacklistToken(token1);
        tokenBlacklistService.blacklistToken(token2);
        tokenBlacklistService.blacklistToken(token3);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(token1));
        assertTrue(tokenBlacklistService.isBlacklisted(token2));
        assertTrue(tokenBlacklistService.isBlacklisted(token3));
        assertFalse(tokenBlacklistService.isBlacklisted("different.token"));
    }

    @Test
    void blacklistToken_WithMultipleNullTokens_ShouldNotAddAnyToBlacklist() {
        // Arrange
        String nullToken1 = null;
        String nullToken2 = null;

        // Act
        tokenBlacklistService.blacklistToken(nullToken1);
        tokenBlacklistService.blacklistToken(nullToken2);

        // Assert
        assertFalse(tokenBlacklistService.isBlacklisted(nullToken1));
        assertFalse(tokenBlacklistService.isBlacklisted(nullToken2));
        // Verify that the blacklist is still empty for null tokens
        assertFalse(tokenBlacklistService.isBlacklisted(null));
    }
}
