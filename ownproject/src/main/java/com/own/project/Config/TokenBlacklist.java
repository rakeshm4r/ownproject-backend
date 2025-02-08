package com.own.project.Config;

import java.util.HashSet;
import java.util.Set;

public class TokenBlacklist {

    private static Set<String> blacklistedTokens = new HashSet<>();  // You can use Redis or DB for persistence

    public static void blacklistToken(String token) {
        blacklistedTokens.add(token);  // Add to blacklist (a simple in-memory approach)
    }

    public static boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);  // Check if token is blacklisted
    }
}


