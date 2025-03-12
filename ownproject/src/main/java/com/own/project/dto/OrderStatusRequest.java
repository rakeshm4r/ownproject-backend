package com.own.project.dto;

import lombok.Data;

@Data
public class OrderStatusRequest {

  private Long ordersStatusId;  // Adjust the type based on your field
  private String orderStatus;
  private String reason;
}
