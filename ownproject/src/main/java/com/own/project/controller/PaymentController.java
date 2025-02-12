package com.own.project.controller;



import com.own.project.Config.JwtUtil;
import com.own.project.Resposes.ApiResponse;
import com.own.project.dao.PaymentDao;
import com.own.project.dto.PaymentRequest;
import com.own.project.exception.PaymentException;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.ProductRepo;
import com.own.project.repository.UserTypeDetailsRepo;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired private PaymentDao paymentDao;

    @Autowired private UserTypeDetailsRepo userRepo;

    @Autowired private ProductRepo productRepo;

 // Razorpay Secret Key from application.properties

    @PostMapping("/saveOrderPaymentDetails")
    public ResponseEntity<?> saveOrderPaymentDetails(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) throws RazorpayException {

        log.info("In PaymentController of saveOrderPaymentDetails()");
        String token = JwtUtil.getTokenFromRequest(request);

        if (token == null) {
            return ResponseEntity.status(401).body("Authorization token missing.");
        }

        Long userId = JwtUtil.getUserIdFromToken(token);

        UserTypeDetails user = userRepo.findById(userId).orElseThrow(() -> new PaymentException("User not found"));
        Product product = productRepo.findById(paymentRequest.getProductId()).orElseThrow(() -> new PaymentException("Product not found"));

        boolean isSaved = paymentDao.saveOrderPaymentDetails(paymentRequest, user, product);
        
        if (isSaved) {
            return ResponseEntity.ok(new ApiResponse("Order Saved Successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to save order payment details.");
            
        }
    }
}


