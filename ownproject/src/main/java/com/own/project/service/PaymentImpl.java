package com.own.project.service;

import java.time.Instant;
import java.time.LocalDateTime;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.own.project.dao.OrdersStatusDao;
import com.own.project.dao.PaymentDao;
import com.own.project.dto.PaymentRequest;
import com.own.project.exception.PaymentException;
import com.own.project.model.Orders;
import com.own.project.model.OrdersStatus;

import com.own.project.model.PaymentDetails;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.OrderRepo;
import com.own.project.repository.OrdersStatusRepo;
import com.own.project.repository.PaymentRepo;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentImpl implements PaymentDao {

  private static final Logger logger = LoggerFactory.getLogger(PaymentImpl.class);

  @Autowired private PaymentRepo paymentRepo;

  @Autowired private OrderRepo orderRepo;

  @Autowired private OrdersStatusDao ordersStatusDao;

  @Autowired private OrdersStatusRepo ordersStatusRepo;

  @Value("${razorpay.key.id}")
  private String razorpayKeyId;

  @Value("${razorpay.key.secret}")
  private String razorpaySecret;

  public PaymentDetails savePayment(PaymentDetails payment) {
    logger.info("In PaymentImpl of savePayment()");
    return paymentRepo.save(payment);
  }

  @Override
  public boolean saveOrderPaymentDetails(PaymentRequest paymentRequest, UserTypeDetails user, Product product) {

    logger.info("In PaymentImpl of saveOrderPaymentDetails()");

    try {
      // Save the order in the database
      Orders orders = new Orders();
      orders.setUser(user);
      orders.setProduct(product);
      orders.setRazorpayOrderId(paymentRequest.getRazorpay_order_id()); // Set Razorpay order ID
      orderRepo.save(orders);
      logger.info("Order saved to database with Razorpay Order ID: {}", orders.getRazorpayOrderId());

      OrdersStatus ordersStatusData = new OrdersStatus();
      ordersStatusData.setUser(user);
      ordersStatusData.setProduct(product);
      ordersStatusData.setOrders(orders);
      ordersStatusData.setBookedOrderdDate(Instant.now());
      ordersStatusData.setDeliverdStatus("pending");

      ordersStatusRepo.save(ordersStatusData);
      logger.info("OrdersStatus saved to database");

       // Link OrdersStatus with Order
       orders.setOrderStatus(ordersStatusData); // Set the status in the Orders entity
       orderRepo.save(orders); // Save again to update with OrderStatus
       logger.info("Order saved to database with ordersStatusData");
 

      PaymentDetails paymentData = new PaymentDetails();
      paymentData.setOrderStatus(ordersStatusData);
      paymentData.setUser(user);
      paymentData.setProduct(product);
      paymentData.setOrders(orders);

      if (paymentRequest.getRazorpay_payment_id() != null) {
            paymentData.setPaymentAmount(paymentRequest.getAmount());
            paymentData.setPaymentStatus("paid");
            paymentData.setPaymentTypeName("online");
            paymentData.setPaymentPaidId(paymentRequest.getRazorpay_payment_id());
      }

      RazorpayClient razorpayClient = null;
      try {
        razorpayClient = new RazorpayClient(razorpayKeyId, razorpaySecret);
      } catch (RazorpayException e) {
        throw new PaymentException("Error getting keys", e);
      }
      String razorpayPaymentId = paymentRequest.getRazorpay_payment_id();
      Payment payment;
      try {
        payment = razorpayClient.payments.fetch(razorpayPaymentId);
        logger.info("Payment : " + payment);
      } catch (RazorpayException e) {
        throw new PaymentException("Error getting razorpayPaymentId", e);
      }

      // Extract details from the Payment object using the correct methods
      String upiTransactionId = null;
      String bankTransactionId = null;
      String cardId = null;
      String bank = null;
      String method = null;
      String upiId = null;
      String walletBank = null;

      method = (String) payment.get("method");

      Object bankDetails = payment.get("bank");
      if (bankDetails != null && !(bankDetails instanceof JSONObject)) {
          // Directly assign the bank value if it's not a JSONObject
          bank = bankDetails.toString();  // "CNRB" in this case
      } else {
          bank = null; // In case it's null or not in the expected format
      }
      logger.info("Bank: " + bank );

      Object walletDetails = payment.get("wallet");
      if (walletDetails != null && !(walletDetails instanceof JSONObject)) {        
           walletBank = walletDetails.toString();
      } else {
           walletBank = null; 
      }
      logger.info("walletBank: " + walletBank);
      

      Object cardDetails = payment.get("card_id");
      if (cardDetails != null && !(cardDetails instanceof JSONObject)) {       
        cardId =cardDetails.toString(); // Extract the cardId information
      } else {
        cardId = null; // In case it's null or not in the expected format
      }
      logger.info("cardId: " + cardId);

      JSONObject upiDetails = (JSONObject) payment.get("upi");
      if (upiDetails != null) {
        upiId = upiDetails.get("vpa").toString(); // Extract the UPI ID (vpa)
      }
      logger.info("upiId: " + upiId);

      // Extract acquirer data (includes transaction IDs, etc.)
      JSONObject acquirerData = (JSONObject) payment.get("acquirer_data");     
      if (acquirerData != null) {
        // Extract UPI transaction ID
        if (acquirerData.has("upi_transaction_id") && !acquirerData.isNull("upi_transaction_id")) {
          upiTransactionId = acquirerData.get("upi_transaction_id").toString(); // Extract the UPI transaction ID
        } else {
          upiTransactionId = "null"; // Fallback if the UPI ID is missing
        }
        logger.info("upiTransactionId: " + upiTransactionId);
        // Extract Bank transaction ID
        if (acquirerData.has("bank_transaction_id") && !acquirerData.isNull("bank_transaction_id")) {
          bankTransactionId = acquirerData.get("bank_transaction_id").toString(); // Extract the Bank transaction ID
        } else {
          bankTransactionId = "null"; // Fallback if the Bank transaction ID is missing
        }
        logger.info("bankTransactionId: " + bankTransactionId);
        // Reset the transaction IDs if "transaction_id" is missing or null
        if (!acquirerData.has("transaction_id") || acquirerData.isNull("transaction_id")) {
          bankTransactionId = null;
          upiTransactionId = null; 
        }
      }

      paymentData.setUpiTransactionId(upiTransactionId);
      paymentData.setBankTransactionId(bankTransactionId);
      paymentData.setCardId(cardId);
      paymentData.setBank(bank);
      paymentData.setMethod(method);
      paymentData.setUpiId(upiId);
      paymentData.setWalletBank(walletBank);
      paymentData.setPaymentDate(LocalDateTime.now());
      
      PaymentDetails savedPaymentData = paymentRepo.save(paymentData);
        if (savedPaymentData.getPaymentId() == null) {
            throw new PaymentException("savedPaymentData is null after saving payment details.");
        }else{
          logger.info("Saved payment data with database ");
        }

      ordersStatusData.setPayment(savedPaymentData);
      ordersStatusRepo.save(ordersStatusData);
      logger.info("OrdersStatus saved to database with payment");

     
      return true;

    } catch (Exception e) {
      logger.error("Error while saving order payment details: ", e);
      return false;
    }
  }

}
