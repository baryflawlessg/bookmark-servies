package com.bookverse.service.impl;

import com.bookverse.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    @Override
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    
    @Override
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    @Override
    public void cleanupExpiredTokens() {
        // In a production environment, you might want to implement
        // token expiration checking and cleanup of expired tokens
        // For now, we'll keep it simple with just the in-memory set
    }
}
