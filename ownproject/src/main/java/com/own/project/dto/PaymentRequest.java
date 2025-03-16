package com.own.project.dto;

import lombok.Data;

@Data
public class PaymentRequest {


  private Long userId;
  private Long productId;
  private double amount;

  private String razorpay_payment_id;
  private String razorpay_order_id;

  private String razorpay_signature;
 
  
  private int quantity;
}

