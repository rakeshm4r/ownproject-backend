package com.own.project.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.own.project.dao.OrderDao;
import com.own.project.model.Orders;
import com.own.project.repository.OrderRepo;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class OrderImpl implements OrderDao {

   private static final Logger log = LoggerFactory.getLogger(OrderImpl.class);

    @Autowired private OrderRepo orderRepo;

    @Autowired private RazorpayClient razorpayClient;

    
   @Override
   public Orders saveOrders(Orders orders) {
      log.info("In OrderImpl of saveOrders()");
         return orderRepo.save(orders);
     
   }
   
  public Order createOrder(double amountInRupees) throws RazorpayException {
        log.info("In RazorpayService of createOrder()");
    
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
            log.info("Requesting Razorpay API to create an order with payload: {}", orderRequest);
    
            // Make the API call to create the order
            Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            
            if (razorpayOrder == null) {
                log.error("Razorpay order creation failed with no response.");
                throw new RazorpayException("Failed to create Razorpay order");
            }
    
            // Extract orderId from the Razorpay response and assign it to the Orders entity
            String razorpayOrderId = razorpayOrder.get("id").toString();
            log.info("Razorpay Order ID: {}", razorpayOrderId);

            return razorpayOrder; // Return the Razorpay order object
        } catch (RazorpayException e) {
            log.error("Error creating Razorpay order: ", e);
            throw e;  // Rethrow the exception to be handled by the controller
        }
    }
}
