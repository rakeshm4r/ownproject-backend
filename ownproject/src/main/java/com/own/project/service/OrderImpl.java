package com.own.project.service;

import java.util.*;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.own.project.dao.OrderDao;
import com.own.project.dto.OrderDetailsDTO;
import com.own.project.dto.UserTypeDetailsDto;
import com.own.project.model.OrdersDetails;
import com.own.project.model.OrdersStatus;
import com.own.project.model.PaymentDetails;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.OrderRepo;
import com.own.project.repository.OrdersStatusRepo;
import com.own.project.repository.PaymentRepo;
import com.own.project.repository.UserTypeDetailsRepo;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class OrderImpl implements OrderDao {

    private static final Logger log = LoggerFactory.getLogger(OrderImpl.class);

    @Autowired private OrderRepo orderRepo;

    @Autowired private RazorpayClient razorpayClient;

    @Autowired private OrdersStatusRepo ordersStatusRepo;

    @Autowired private PaymentRepo paymentDetailsRepo;

    @Autowired private UserTypeDetailsRepo userTypeDetailsRepo;

    @Autowired private UserTypeDetailsImpl userTypeDetailsImpl;
    
   @Override
   public OrdersDetails saveOrders(OrdersDetails orders) {
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

    public List<OrderDetailsDTO> getUserOrderDetails(Long userId) {
        log.info("In OrderImpl of getUserOrderDetails()");

        Optional<UserTypeDetails> userOptional = userTypeDetailsRepo.findById(userId);
        if (!userOptional.isPresent()) {
            return Collections.emptyList(); // Return an empty list if user is not found
        }
        UserTypeDetails user = userOptional.get();
    
        List<OrdersDetails> orders = orderRepo.findByUser(user);
    
        if (orders == null || orders.isEmpty()) {
            return Collections.emptyList(); // Return empty list if no orders found
        }

        return orders.stream()
            .map(order -> {
                // Fetch related OrderStatus, PaymentDetails, and Product in one go
                OrdersStatus orderStatus = ordersStatusRepo.findByOrders(order);
                PaymentDetails paymentDetails = paymentDetailsRepo.findByOrders(order);
                Product product = order.getProduct();
    
                // Create and populate OrderDetailsDTO
                OrderDetailsDTO orderDetailsDto = new OrderDetailsDTO();
                orderDetailsDto.setUserName(user.getUserName());
                orderDetailsDto.setProductName(product.getProductName());
                orderDetailsDto.setOrdersStatusId(orderStatus.getOrdersStatusId());
                orderDetailsDto.setOrderStatus(orderStatus.getDeliverdStatus());
                orderDetailsDto.setBookedOrderdDate(orderStatus.getBookedOrderdDate());
                orderDetailsDto.setDeliveredOrderdDate(orderStatus.getDeliverdOrderdDate());
                orderDetailsDto.setPaymentStatus(paymentDetails.getPaymentStatus());
                orderDetailsDto.setPaymentAmount(paymentDetails.getPaymentAmount());
                orderDetailsDto.setPaymentTypeName(paymentDetails.getPaymentTypeName());
                orderDetailsDto.setOrderNumber(order.getOrderNumber());
                orderDetailsDto.setReason(orderStatus.getReason());
                orderDetailsDto.setModifiedOrderdStatusDate(orderStatus.getModifiedOrderdStatusDate());

                UserTypeDetailsDto userTypeDetailsDto = userTypeDetailsImpl.mapUserToUserTypeDetailsDto(user);
                orderDetailsDto.setUserDetails(userTypeDetailsDto);

                return orderDetailsDto;
            })
            .collect(Collectors.toList()); // Collect the results into a list
    }  

public List<OrderDetailsDTO> getOrdersByStatus(String deliverdStatus) {
    log.info("In OrderImpl of getOrdersByStatus()");
    List<OrdersDetails> ordersDetailsList = orderRepo.findAll();

    if (ordersDetailsList == null || ordersDetailsList.isEmpty()) {
        return Collections.emptyList();
    }

    // Filter orders based on the status    

    List<OrderDetailsDTO> orderDetailsList = new ArrayList<>();

    orderDetailsList =  ordersDetailsList.stream()
                    .filter(order -> order.getOrderStatus().getDeliverdStatus().equalsIgnoreCase(deliverdStatus))
                    .map(this::convertToOrderDetailsDTO)
                    .collect(Collectors.toList());

    return orderDetailsList;
}

public OrderDetailsDTO convertToOrderDetailsDTO(OrdersDetails ordersStatus) {
    OrderDetailsDTO orderDetailsDto = new OrderDetailsDTO();

    // Mapping fields from OrdersDetails to OrderDetailsDTO
    orderDetailsDto.setOrderStatus(ordersStatus.getOrderStatus().getDeliverdStatus());
    orderDetailsDto.setBookedOrderdDate(ordersStatus.getOrderStatus().getBookedOrderdDate());
    orderDetailsDto.setDeliveredOrderdDate(ordersStatus.getOrderStatus().getDeliverdOrderdDate());
    orderDetailsDto.setProductName(ordersStatus.getProduct().getProductName());
    orderDetailsDto.setPaymentAmount(ordersStatus.getOrderStatus().getPayment().getPaymentAmount());
    orderDetailsDto.setUserName(ordersStatus.getUser().getUserName());
    orderDetailsDto.setPaymentStatus(ordersStatus.getOrderStatus().getPayment().getPaymentStatus());
    orderDetailsDto.setPaymentTypeName(ordersStatus.getOrderStatus().getPayment().getPaymentTypeName());
    orderDetailsDto.setOrderNumber(ordersStatus.getOrderNumber());
    orderDetailsDto.setOrdersStatusId(ordersStatus.getOrderStatus().getOrdersStatusId());
    orderDetailsDto.setReason(ordersStatus.getOrderStatus().getReason());
    orderDetailsDto.setModifiedOrderdStatusDate(ordersStatus.getOrderStatus().getModifiedOrderdStatusDate());
    // Set the modified user if available
    Optional.ofNullable(ordersStatus.getOrderStatus().getModifiedUserByOrdStatus())
            .map(modifiedUserId -> userTypeDetailsRepo.findById(modifiedUserId).orElse(null))
            .map(UserTypeDetails::getUserName)
            .ifPresent(orderDetailsDto::setModifiedUser);

    // Set user details
    UserTypeDetailsDto userTypeDetailsDto = userTypeDetailsImpl.mapUserToUserTypeDetailsDto(ordersStatus.getUser());
    orderDetailsDto.setUserDetails(userTypeDetailsDto);

    return orderDetailsDto;
}


@Override
public void updateOrderStatus(Long  ordersStatusId, String orderStatus,String reason,Long modifiedUserId) {
    log.info("In OrderImpl of updateOrderStatus()");
    OrdersStatus ordersStatusData = ordersStatusRepo.findById(ordersStatusId)
       .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order status not found"));

    ordersStatusData.setDeliverdStatus(orderStatus);
    ordersStatusData.setReason(reason);
    ordersStatusData.setModifiedUserByOrdStatus(modifiedUserId);  
    ordersStatusRepo.save(ordersStatusData);
}


public List<OrderDetailsDTO> getAllOrderDetails() {
    log.info("In OrderImpl of getAllOrderDetails()");
    
    List<OrdersStatus> ordersStatusData = ordersStatusRepo.findPackedAndShippedOrders();
    
    if (ordersStatusData == null || ordersStatusData.isEmpty()) {
        return Collections.emptyList(); 
    }
    List<OrderDetailsDTO> orderDetailsList = new ArrayList<>();
    
    for (OrdersStatus ordersStatus : ordersStatusData) {
      
        OrderDetailsDTO orderDetailsDto = new OrderDetailsDTO();
        orderDetailsDto.setOrderStatus(ordersStatus.getDeliverdStatus());
        orderDetailsDto.setBookedOrderdDate(ordersStatus.getBookedOrderdDate());
        orderDetailsDto.setDeliveredOrderdDate(ordersStatus.getDeliverdOrderdDate());
        orderDetailsDto.setProductName(ordersStatus.getProduct().getProductName());
        orderDetailsDto.setPaymentAmount(ordersStatus.getPayment().getPaymentAmount());
        orderDetailsDto.setUserName(ordersStatus.getUser().getUserName());
        orderDetailsDto.setPaymentStatus(ordersStatus.getPayment().getPaymentStatus());
        orderDetailsDto.setPaymentTypeName(ordersStatus.getPayment().getPaymentTypeName());
        orderDetailsDto.setOrderNumber(ordersStatus.getOrders().getOrderNumber());
        orderDetailsDto.setOrdersStatusId(ordersStatus.getOrdersStatusId());

        UserTypeDetailsDto userTypeDetailsDto = new UserTypeDetailsDto();
        userTypeDetailsDto.setUserName(ordersStatus.getUser().getUserName());
        userTypeDetailsDto.setEmailId(ordersStatus.getUser().getEmailId());
        userTypeDetailsDto.setMobileNo(ordersStatus.getUser().getMobileNo());
        userTypeDetailsDto.setAddress(ordersStatus.getUser().getAddress());
        userTypeDetailsDto.setCity(ordersStatus.getUser().getCity());
        userTypeDetailsDto.setState(ordersStatus.getUser().getState());
        userTypeDetailsDto.setPinCode(ordersStatus.getUser().getPinCode());
        userTypeDetailsDto.setCountry(ordersStatus.getUser().getCountry());
        
        orderDetailsDto.setUserDetails(userTypeDetailsDto);
        orderDetailsList.add(orderDetailsDto);
    }
    
    return orderDetailsList;
}
    
}
