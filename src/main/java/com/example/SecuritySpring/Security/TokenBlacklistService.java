package com.example.SecuritySpring.Security;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Boolean> tokenBlacklist = new ConcurrentHashMap<>();

    public void blacklistToken(String token) {
        tokenBlacklist.put(token, true);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.getOrDefault(token, false);
    }
}
