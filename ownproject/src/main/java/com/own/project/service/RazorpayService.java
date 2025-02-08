package com.own.project.service;

import com.own.project.model.Orders;
import com.own.project.model.OrdersStatus;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.OrderRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayException;
import com.razorpay.RazorpayClient;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RazorpayService {

    private static final Logger logger = LoggerFactory.getLogger(RazorpayService.class);

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private OrderRepo ordersRepository;

    public Order createOrder(double amountInRupees, UserTypeDetails user, Product product) throws RazorpayException {
        logger.info("In RazorpayService of createOrder()");
    
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amountInRupees * 100));  // Convert to paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_receipt_" + System.currentTimeMillis());
        
        // Using a map for 'notes' field
        JSONObject notes = new JSONObject();
        notes.put("order_details", "Order created for product purchase");
        orderRequest.put("notes", notes);  // Map for notes field
        
        orderRequest.put("payment_capture", 1);  // Auto capture payment
    
        try {
            // Log the request
            logger.info("Requesting Razorpay API to create an order with payload: {}", orderRequest);
    
            // Make the API call to create the order
            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            
            if (razorpayOrder == null) {
                logger.error("Razorpay order creation failed with no response.");
                throw new RazorpayException("Failed to create Razorpay order");
            }
    
            // Extract orderId from the Razorpay response and assign it to the Orders entity
            String razorpayOrderId = razorpayOrder.get("id").toString();
            logger.info("Razorpay Order ID: {}", razorpayOrderId);
            
            // Save the order in the database
            Orders orders = new Orders();
            orders.setUser(user);
            orders.setProduct(product);
          //  orders.setOrderId(razorpayOrderId);  // Set the orderId received from Razorpay
            
            OrdersStatus ordersStatus = new OrdersStatus();
            ordersStatus.setDeliverdStatus("pending");  // Default status
            orders.setOrderStatus(ordersStatus);
    
            // Persist the order in the database
            ordersRepository.save(orders);
            logger.info("Order saved to database with Order ID: {}", orders.getOrderId());
    
            return razorpayOrder; // Return the Razorpay order object
        } catch (RazorpayException e) {
            logger.error("Error creating Razorpay order: ", e);
            throw e;  // Rethrow the exception to be handled by the controller
        }
    }
    
    
}

