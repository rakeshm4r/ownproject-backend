package com.own.project.dto;



import lombok.Data;

@Data
public class OrdersRequest {

  private Long userId;
  private Long productId;
  private double amount;
  
}
