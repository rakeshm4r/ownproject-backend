package com.own.project.Config;

import com.razorpay.RazorpayClient; // Corrected import for newer versions

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;

@Configuration
public class RazorpayConfig {

    private static final Logger log = LoggerFactory.getLogger(RazorpayConfig.class);

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    
    @Bean
    public RazorpayClient razorpayClient() {
        RazorpayClient client = null;
        try {
            client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        } catch (Exception e) {
            log.error("Error initializing RazorpayClient", e);
        }
        return client;
    }
}
