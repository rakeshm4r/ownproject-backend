package com.own.project.dao;

import com.own.project.model.Orders;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

public interface OrderDao {

  public Orders saveOrders(Orders orders);

  public Order createOrder(double amount) throws RazorpayException;

  
  
}
