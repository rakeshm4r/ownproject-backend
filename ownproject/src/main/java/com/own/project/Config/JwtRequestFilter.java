package com.own.project.Config;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;



import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;    

     private final UserDetailsService userDetailsService; // This service can load user details based on username

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;

    }
private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
      // Skip JWT filter for login, signIn, check-email, and reset-password endpoints
      if (isPublicEndpoint(request)) {
        filterChain.doFilter(request, response);  // Proceed without JWT filtering
        return;
    }
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extract token
            try {
                username = jwtUtil.extractUsername(jwt); // Extract username from token
                // Logging for debugging
                log.info("JWT token extracted, username: {}", username);
            } catch (ExpiredJwtException e) {
                log.error("JWT token has expired", e);
            } catch (Exception e) {
                log.error("Error parsing JWT token", e);
            }
        } else {
            log.warn("Authorization header missing or does not start with 'Bearer '");
        }

        // Check if token is valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username) && !TokenBlacklist.isTokenBlacklisted(jwt)) {              
        
                log.info("JWT token is valid for user: {}", username);

                // Load user details based on username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create an authentication object based on user details and token
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Set the authentication object in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("Token is invalid.");
            }
        }

        filterChain.doFilter(request, response);
    }

        // Method to check if the request URI is one of the public endpoints
        private boolean isPublicEndpoint(HttpServletRequest request) {
            String uri = request.getRequestURI();
            // Define the list of public endpoints that do not require JWT authentication
            return uri.contains("/api/users/login") || 
                   uri.contains("/api/users/signIn") || 
                   uri.contains("/api/users/check-email") || 
                   uri.contains("/api/users/reset-password") || 
                   uri.contains("/api/users/send-reset-link");
        }
}

