package com.own.project.dto;

import com.own.project.model.Orders;
import com.own.project.model.OrdersStatus;
import com.own.project.model.Payment;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;

import lombok.Data;

@Data
public class Order_OS_Pymt {

  private Product product;
  private UserTypeDetails UserTypeDetails;
  private Orders orders;
  private OrdersStatus ordersStatus;
  private Payment payment;
  
}
