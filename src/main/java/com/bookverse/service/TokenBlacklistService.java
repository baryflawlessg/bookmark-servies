package com.bookverse.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface TokenBlacklistService {
    void blacklistToken(String token);
    boolean isBlacklisted(String token);
    void cleanupExpiredTokens();
}
