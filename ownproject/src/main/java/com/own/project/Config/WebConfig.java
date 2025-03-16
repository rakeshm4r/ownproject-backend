package com.own.project.Config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Inside addCorsMappings() of WebConfig.java");
        registry.addMapping("/api/**") // Apply CORS configuration to all "/api/**" endpoints
                .allowedOrigins("http://localhost:4200") // Allow requests from the Angular frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow specific HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow credentials (e.g., cookies, authorization headers)                
    }
}
