// package com.own.project.service;

// import com.own.project.dao.OrdersStatusDao;
// import com.own.project.dao.PaymentDao;
// import com.own.project.model.Orders;
// import com.own.project.model.OrdersStatus;
// import com.own.project.model.Payment;
// import com.own.project.model.Product;
// import com.own.project.model.UserTypeDetails;
// import com.own.project.repository.OrderRepo;
// import com.razorpay.Order;
// import com.razorpay.RazorpayException;
// import com.razorpay.RazorpayClient;

// import java.time.Instant;

// import org.json.JSONObject;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// @Service
// public class RazorpayService {

//     private static final Logger logger = LoggerFactory.getLogger(RazorpayService.class);

//     @Autowired private RazorpayClient razorpayClient;

//     @Autowired private OrderRepo ordersRepository;

//     @Autowired private PaymentDao paymentDao;

//     @Autowired private OrdersStatusDao ordersStatusDao;

//     public Orders createOrder(double amountInRupees, UserTypeDetails user, Product product) throws RazorpayException {
//         logger.info("In RazorpayService of createOrder()");
    
//         JSONObject orderRequest = new JSONObject();
//         int amountInPaise = (int)(amountInRupees * 100);  // Convert to paise
//         orderRequest.put("amount", amountInPaise);  // Set amount in paise
//         orderRequest.put("currency", "INR");
//         orderRequest.put("receipt", "order_receipt_" + System.currentTimeMillis());
        
//         // Using a map for 'notes' field
//         JSONObject notes = new JSONObject();
//         notes.put("order_details", "Order created for product purchase");
//         orderRequest.put("notes", notes);  // Map for notes field
        
//         orderRequest.put("payment_capture", 1);  // Auto capture payment
    
//         try {
//             // Log the request
//             logger.info("Requesting Razorpay API to create an order with payload: {}", orderRequest);
    
//             // Make the API call to create the order
//             Order razorpayOrder = razorpayClient.orders.create(orderRequest);
            
//             if (razorpayOrder == null) {
//                 logger.error("Razorpay order creation failed with no response.");
//                 throw new RazorpayException("Failed to create Razorpay order");
//             }
    
//             // Extract orderId from the Razorpay response and assign it to the Orders entity
//             String razorpayOrderId = razorpayOrder.get("id").toString();
//             logger.info("Razorpay Order ID: {}", razorpayOrderId);
            
//             // Save the order in the database
//             Orders orders = new Orders();
//             orders.setUser(user);
//             orders.setProduct(product);
//             orders.setRazorpayOrderId(razorpayOrderId);  // Set Razorpay order ID // Set the orderId received from Razorpay
            
//             ordersRepository.save(orders);
//             logger.info("Order saved to database with Razorpay Order ID: {}", orders.getRazorpayOrderId());

//             OrdersStatus ordersStatusData = new OrdersStatus();
//             ordersStatusData.setUser(user);
//             ordersStatusData.setProduct(product);            
//             ordersStatusData.setOrders(orders);
//             ordersStatusData.setBookedOrderdDate(Instant.now());
//             ordersStatusData.setDeliverdStatus("pending");

//             ordersStatusDao.savOrdersStatus(ordersStatusData);
//             logger.info("OrdersStatus saved to database ");

//              // Link OrdersStatus with Order
//             orders.setOrderStatus(ordersStatusData);  // Set the status in the Orders entity
//             ordersRepository.save(orders);  // Save again to update with OrderStatus
//             logger.info("Order saved to database with ordersStatusData");

//             Payment  paymentData = new Payment();
//             paymentData.setOrderStatus(ordersStatusData);
//             paymentData.setUser(user);
//             paymentData.setProduct(product);
//             paymentData.setOrders(orders);
//             paymentData.setPaymentStatus("pending");
//             paymentData.setPaymentAmount(amountInRupees);
//             paymentData.setPaymentDate(null);
//             paymentData.setPaymentTypeName("cod");
//             paymentData.setPaymentPaidId("");
//             paymentDao.savePayment(paymentData);
//             logger.info("Payment saved to database");

//             ordersStatusData.setPayment(paymentData);
//             ordersStatusDao.savOrdersStatus(ordersStatusData);
//             logger.info("OrdersStatus saved to database with payment");

//             return orders; // Return the Razorpay order object

//         } catch (RazorpayException e) {
//             logger.error("Error creating Razorpay order: ", e);
//             throw e;  // Rethrow the exception to be handled by the controller
//         }
//     }
    
    
// }

