package com.own.project.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.own.project.Config.JwtUtil;
import com.own.project.Resposes.ApiResponse;
import com.own.project.dao.CartDao;
import com.own.project.dto.CartDto;
import com.own.project.exception.CartException;
import com.own.project.model.Cart;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.ProductRepo;
import com.own.project.repository.UserTypeDetailsRepo;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    @Autowired private CartDao cartDao;

    @Autowired private ProductRepo productRepo;

    @Autowired private UserTypeDetailsRepo userRepo;

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestBody String productName, HttpServletRequest request) {
        log.info("In CartController of addToCart()");
        try {
            String token = JwtUtil.getTokenFromRequest(request);

            if (token == null) {
                return ResponseEntity.status(401).body("Authorization token missing.");
            }

            Long userId = JwtUtil.getUserIdFromToken(token);

            Product product = productRepo.findByProductName(productName);
            if (product == null) {
                throw new RuntimeException("Product not found with name: " + productName);
            }

            UserTypeDetails user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
            boolean success = cartDao.addToCart(product, user);

            if (success) {
                return ResponseEntity.ok(new ApiResponse("Item added to the cart successfully.", true, productName));
            } else {
                return ResponseEntity.status(400).body("Failed to add item to the cart.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Bad Request: " + e.getMessage());
        } catch (Exception e) {
            // Generic fallback for unexpected errors
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    @GetMapping("/getCartDetsByUser")
    public List<?> getCartDetails(HttpServletRequest request) {
        log.info("In CartController of getCartDetails()");
        String token = JwtUtil.getTokenFromRequest(request);

        if (token == null) {
            throw new CartException("Authorization token missing.");
        }
      
        Long userId = JwtUtil.getUserIdFromToken(token);

        List<Cart> carts = cartDao.getCartDetailsByUserId(userId);

        List<CartDto> allCartDtos = carts.stream().map(CartDto::new).collect(Collectors.toList());
        return allCartDtos;
    }

    @PostMapping("/removeProductFromCart")
    public void removeProductFromCart(@RequestBody Long cartId, HttpServletRequest request) {
        log.info("In CartController of removeProductFromCart()");
        String token = JwtUtil.getTokenFromRequest(request);    

        if (token == null) {
            throw new CartException("Authorization token missing.");
        }
       // Long userId = JwtUtil.getUserIdFromToken(token);
        cartDao.removeProductFromCart(cartId);

    }

}