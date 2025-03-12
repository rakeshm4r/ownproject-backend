package com.own.project.Config;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenBlacklist {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklist.class);

    private static Set<String> blacklistedTokens = new HashSet<>();  // You can use Redis or DB for persistence

    public static void blacklistToken(String token) {
        logger.info("inside blacklistToken() of TokenBlacklist.java");
        blacklistedTokens.add(token);  // Add to blacklist (a simple in-memory approach)
    }

    public static boolean isTokenBlacklisted(String token) {
        logger.info("inside isTokenBlacklisted() of TokenBlacklist.java");
        return blacklistedTokens.contains(token);  // Check if token is blacklisted
    }
}


