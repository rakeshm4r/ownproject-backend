package com.own.project.dto;

import lombok.Data;

@Data
public class PaymentRequest {
  private Long userId;
  private Long productId;
  private double amount;

 
}

