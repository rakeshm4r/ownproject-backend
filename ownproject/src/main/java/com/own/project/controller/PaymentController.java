package com.own.project.controller;


import com.own.project.Config.JwtUtil;
import com.own.project.dto.PaymentRequest;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.ProductRepo;
import com.own.project.repository.UserTypeDetailsRepo;
import com.own.project.service.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private RazorpayService razorpayService;

    @Autowired
    private UserTypeDetailsRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

     @PostMapping("/createOrder")
    public ResponseEntity<?> createOrder(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        try {
             String token = JwtUtil.getTokenFromRequest(request);

            if (token == null) {
                return ResponseEntity.status(401).body("Authorization token missing.");
            }

            Long userId = JwtUtil.getUserIdFromToken(token);

            UserTypeDetails user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Product product = productRepo.findById(paymentRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Create the Razorpay order
            Order razorpayOrder = razorpayService.createOrder(paymentRequest.getAmount(), user, product);
            return ResponseEntity.ok(razorpayOrder);
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
           }
    }
}


