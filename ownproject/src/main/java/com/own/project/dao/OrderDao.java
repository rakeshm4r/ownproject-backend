package com.own.project.dao;


import java.util.List;

import com.own.project.dto.OrderDetailsDTO;
import com.own.project.model.OrdersDetails;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface OrderDao {

  public OrdersDetails saveOrders(OrdersDetails orders);

  public Order createOrder(double amount) throws RazorpayException;

  public List<?> getUserOrderDetails(Long userId);

  public List<OrderDetailsDTO> getOrdersByStatus(String deliverdStatus);

  public void updateOrderStatus(Long  ordersStatusId, String orderStatus,String reason,Long userId);

  public List<OrderDetailsDTO> getAllOrderDetails();

  

  

  
  
}
