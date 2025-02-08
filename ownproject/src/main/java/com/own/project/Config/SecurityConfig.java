package com.own.project.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtUtil jwtUtil, JwtRequestFilter jwtRequestFilter) {
        this.jwtUtil = jwtUtil;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    // Configure HTTP security
    @SuppressWarnings({ "removal", "deprecation" })
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests()
                .requestMatchers("/api/users/login", "/api/users/signIn", "/api/users/check-email",
                        "/api/users/reset-password", "/api/users/send-reset-link")
                .permitAll() // Permit public login and register
                .requestMatchers("/api/**").authenticated() // Secure all /api/** paths (cart, products, etc.)
                // .requestMatchers("/api/form/submit/**").hasRole("Admin")
                .anyRequest().authenticated() // Secure all other requests
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean for AuthenticationManager (needed for authentication)
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

}
