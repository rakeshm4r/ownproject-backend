package com.own.project.service;

import java.time.LocalDateTime;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.own.project.Util_Services.NameUtils;
import com.own.project.dao.PaymentDao;
import com.own.project.dto.PaymentRequest;

import com.own.project.exception.PaymentException;
import com.own.project.model.OrdersDetails;
import com.own.project.model.OrdersStatus;
import com.own.project.model.PaymentDetails;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.OrderRepo;
import com.own.project.repository.OrdersStatusRepo;
import com.own.project.repository.PaymentRepo;
import com.own.project.repository.ProductRepo;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Service
public class PaymentImpl implements PaymentDao {

  private static final Logger logger = LoggerFactory.getLogger(PaymentImpl.class);

  @Autowired private PaymentRepo paymentRepo;

  @Autowired private OrderRepo orderRepo;

  @Autowired private OrdersStatusRepo ordersStatusRepo;

  @Autowired private ProductRepo productRepo;

  @Value("${razorpay.key.id}")
  private String razorpayKeyId;

  @Value("${razorpay.key.secret}")
  private String razorpaySecret;

  public PaymentDetails savePayment(PaymentDetails payment) {
    logger.info("In PaymentImpl of savePayment()");
    return paymentRepo.save(payment);
  }
  @Autowired
  private NameUtils nameUtils; 

  @Override
  @Transactional
  public boolean saveOrderPaymentDetails(PaymentRequest paymentRequest, UserTypeDetails user, Product product) {

    logger.info("In PaymentImpl of saveOrderPaymentDetails()");

    try {
      if (product.getNoOfItems() > 0) {
        product.setNoOfItems(product.getNoOfItems() - paymentRequest.getQuantity()); // Reduce the count by 1
        // Save the updated product back to the database (this assumes you're using a repository for saving)
        productRepo.save(product);  // Make sure you have the productRepository injected into this class
        logger.info("Product quantity updated successfully");
       
    } else {
        logger.error("No items left in stock for the product");
        return false;
    }
      

      // Save the order in the database
      OrdersDetails orders = new OrdersDetails();
      orders.setUser(user);
      orders.setProduct(product);      
      orders.setRazorpayOrderId(paymentRequest.getRazorpay_order_id()); // Set Razorpay order ID

      String orderNumber = nameUtils.generateOrderNumber();    
      orders.setOrderNumber(orderNumber);

      orderRepo.save(orders);
      logger.info("Order saved to database with Razorpay Order ID: {}", orders.getRazorpayOrderId());

      OrdersStatus ordersStatusData = new OrdersStatus();
      ordersStatusData.setUser(user);
      ordersStatusData.setProduct(product);
      ordersStatusData.setOrders(orders);
      ordersStatusData.setBookedOrderdDate(LocalDateTime.now().withNano(0));
      ordersStatusData.setDeliverdStatus("confirmed");
      ordersStatusData.setModifiedUserByOrdStatus(user.getUserId());
      ordersStatusData.setModifiedOrderdStatusDate(LocalDateTime.now().withNano(0));
      ordersStatusData.setItemsQuantity(paymentRequest.getQuantity());
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
        paymentData.setRazorpayPaymentId(paymentRequest.getRazorpay_payment_id());
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

      String bank = null;
      String method = null;
      String upiId = null;
      String walletBank = null;

      String cardId = null;
      String cardLast4Digits = null;
      String cardType = null;
      String card_issuer_bank_name = null;
      String card_network = null;

      method = (String) payment.get("method");
      logger.info("method: " + method);

      Object bankDetails = payment.get("bank");
      if (bankDetails != null && !(bankDetails instanceof JSONObject)) {
        // Directly assign the bank value if it's not a JSONObject
        bank = bankDetails.toString(); // "CNRB" in this case
      } else {
        bank = null; // In case it's null or not in the expected format
      }
      logger.info("Bank: " + bank);

      Object walletDetails = payment.get("wallet");
      if (walletDetails != null && !(walletDetails instanceof JSONObject)) {
        walletBank = walletDetails.toString();
      } else {
        walletBank = null;
      }
      logger.info("walletBank: " + walletBank);

      Object cardDetails = payment.get("card_id");
      if (cardDetails != null && !(cardDetails instanceof JSONObject)) {
        cardId = cardDetails.toString(); // Extract the cardId information
      } else {
        cardId = null; // In case it's null or not in the expected format
      }
      logger.info("cardId: " + cardId);

      JSONObject cardData = (JSONObject) payment.get("card");
      if (cardData != null) {
        cardLast4Digits = cardData.get("last4").toString();
        cardType = cardData.get("type").toString();
        card_issuer_bank_name = cardData.get("issuer").toString();
        card_network = cardData.get("network").toString();
        logger.info("cardLast4Digits: " + cardLast4Digits + " cardType: " + cardType + " card_issuer_bank_name: "
            + card_issuer_bank_name + " card_network: " + card_network);
      }

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
       
      }

      paymentData.setUpiTransactionId(upiTransactionId);
      paymentData.setBankTransactionId(bankTransactionId);

      paymentData.setBank(bank);
      paymentData.setMethod(method);
      paymentData.setUpiId(upiId);
      paymentData.setWalletBank(walletBank);
      paymentData.setPaymentDate(LocalDateTime.now().withNano(0));

      paymentData.setCardId(cardId);
      paymentData.setCard_last_digits(cardLast4Digits);
      paymentData.setCardType(cardType);
      paymentData.setCard_issuer_bank_name(card_issuer_bank_name);
      paymentData.setCard_network(card_network);

      PaymentDetails savedPaymentData = paymentRepo.save(paymentData);
      if (savedPaymentData.getPaymentId() == null) {
        throw new PaymentException("savedPaymentData is null after saving payment details.");
      } else {
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
