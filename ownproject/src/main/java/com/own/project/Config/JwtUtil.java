package com.own.project.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static String secretKey = "RqxPOuVfHoBA8Uq40MhJvfY6qEHOOWWvg6N9W9vt23s="; // Static key for demonstration

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static SecretKey generateKey() {
        byte[] decode = Decoders.BASE64.decode(getSecretKey());
        return Keys.hmacShaKeyFor(decode);
    }

    // Make the getSecretKey() method static
    public static String getSecretKey() {
        return secretKey;
    }

    @SuppressWarnings("deprecation")
    public static String generateToken(String username, String role, Long userId) {
        logger.info("Generating token for user: " + username + " with role: " + role);

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // 1 day expiration time (24 hours)
                .signWith(generateKey())  // Now, the static method generateKey() can be called
                .compact();
    }
    

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract claims
    @SuppressWarnings("deprecation")
    public Claims extractAllClaims(String token) {
        return Jwts.parser() // Correct parser for version 0.12.6
                .setSigningKey(secretKey)  // Set the signing key
                .build() // Build the parser
                .parseClaimsJws(token)  // Parse the JWT
                .getBody();  // Extract the body (claims)
    }

    // Extract specific claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Validate the token
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Check if the token is valid
    public Boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    // Utility method to get JWT token from Authorization header
    public static String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Remove "Bearer " part
        }
        return null; // Token is not found
    }

    // Utility method to parse JWT token and get claims
    @SuppressWarnings("deprecation")
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()  // Use the same key as the one used to sign the token
                .parseClaimsJws(token)
                .getBody();
    }

    // Method to get userId from token
    public static Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? Long.valueOf(claims.get("userId").toString()) : null;
    }

    // Method to get role from token
    public static String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("role", String.class) : null;
    }
}
