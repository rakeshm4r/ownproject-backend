package com.own.project.controller;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.own.project.Config.JwtUtil;
import com.own.project.dao.OrderDao;
import com.own.project.dao.OrdersStatusDao;
import com.own.project.dao.PaymentDao;
import com.own.project.model.Orders;
import com.own.project.model.OrdersStatus;
import com.own.project.model.Payment;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.OrdersStatusRepo;
import com.own.project.repository.PaymentRepo;
import com.own.project.repository.ProductRepo;
import com.own.project.repository.UserTypeDetailsRepo;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/orders")
public class OrderController {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);

  @Autowired private OrderDao orderDao;

  @Autowired private OrdersStatusDao ordersStatusDao;

  @Autowired private PaymentDao paymentDao;

  @Autowired private UserTypeDetailsRepo userRepo;

  @Autowired private ProductRepo productRepo;

  @Autowired private OrdersStatusRepo ordersStatusRepo;

  @Autowired private PaymentRepo paymentRepo;


  @PostMapping("/saveOrder")
  public ResponseEntity<?> saveOrder(HttpServletRequest request) {
    log.info("In OrderController of saveOrder()");

    String token = JwtUtil.getTokenFromRequest(request);

    if (token == null) {
      return ResponseEntity.status(401).body("Authorization token missing.");
    }

    Long userId = JwtUtil.getUserIdFromToken(token);

    Long productId=0l;

    Long ordersStatusId=0l;

    Long paymentId=0l;
    
    UserTypeDetails user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found with userId: " + userId));

    Product product = productRepo.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with productId: " + productId));

    OrdersStatus ordersStatus=ordersStatusRepo.findById(ordersStatusId).orElseThrow(() -> new RuntimeException("OrdersStatus not found with ordersStatusId: " + ordersStatusId));
    
    Payment payment =paymentRepo.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found with paymentId: " + paymentId));
    
    Orders orders = new Orders();
    orders.setUser(user);
    orders.setProduct(product);
    orders.setOrderStatus(ordersStatus);   

    orderDao.saveOrders(orders);

    OrdersStatus ordersStatusData = new OrdersStatus();
    ordersStatusData.setUser(user);
    ordersStatusData.setProduct(product);
    ordersStatusData.setPayment(payment);
    ordersStatusData.setOrders(orders);
    ordersStatusData.setBookedOrderdDate(Instant.now());
    ordersStatusData.setDeliverdStatus("pending");

    ordersStatusDao.savOrdersStatus(ordersStatusData);


    Payment  paymentData = new Payment();
    paymentData.setOrderStatus(ordersStatusData);
    paymentData.setUser(user);
    paymentData.setProduct(product);
    paymentData.setOrders(orders);
    paymentData.setPaymentStatus(null);
    paymentData.setPaymentAmount(0);
    paymentData.setPaymentDate(null);
    paymentData.setPaymentTypeName(null);

    paymentDao.savePayment(paymentData);
    
    return null;
  }
}
