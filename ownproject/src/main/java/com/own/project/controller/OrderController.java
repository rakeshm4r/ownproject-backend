package com.own.project.controller;



import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.own.project.Config.JwtUtil;
import com.own.project.dao.OrderDao;
import com.own.project.dto.OrdersRequest;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/orders")
public class OrderController {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);

  @Autowired private OrderDao orderDao;

  @PostMapping("/createOrder")
  public ResponseEntity<?> createOrder(@RequestBody OrdersRequest orderRequest, HttpServletRequest request) {

       log.info("In OrderController of createOrder()");

        String token = JwtUtil.getTokenFromRequest(request);

        if (token == null) {
            return ResponseEntity.status(401).body("Authorization token missing.");
        }

        // Create the Razorpay order
        Order razorpayOrder=null;
        try {
          razorpayOrder = orderDao.createOrder(orderRequest.getAmount());
        } catch (RazorpayException e) {
          
          e.printStackTrace();
          return ResponseEntity.status(500).body("Failed to create Razorpay order.");
        }
        // Extract razorpayOrderId and amount
        String razorpayOrderId = razorpayOrder.get("id").toString();
        int amountInPaise = razorpayOrder.get("amount"); // This returns the amount in paise
    
        // Convert amount from paise (integer) to rupees (double)
        double amountInRupees = amountInPaise / 100.0; // Convert paise to rupees (as a double)

        // Create a map for response
        Map<String, Object> response = new HashMap<>();
        response.put("razorpayOrderId", razorpayOrderId);
        response.put("amount", amountInRupees);

        // Return the response map
        return ResponseEntity.ok(response);
    }
}
