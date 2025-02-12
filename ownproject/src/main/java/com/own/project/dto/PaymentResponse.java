package com.own.project.dto;

import lombok.Data;

@Data
public class PaymentResponse {

  private String orderId;
  private double amount;

  private String razorpayPaymentId;
  private String razorpayOrderId;
  private String razorpaySignature;

  public PaymentResponse(String orderId, double amount) {
    this.orderId = orderId;
    this.amount = amount;
  }
  
}
