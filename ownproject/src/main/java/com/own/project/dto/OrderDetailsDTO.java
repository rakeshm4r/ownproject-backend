package com.own.project.dto;

import java.time.LocalDateTime;

import lombok.Data;
@Data
public class OrderDetailsDTO {


  private String userName;
  private String productName;
  private String orderStatus;
  private double paymentAmount;
  private String paymentStatus;
  private LocalDateTime bookedOrderdDate;
  private LocalDateTime deliveredOrderdDate;
  private String paymentTypeName;
  private Long ordersStatusId;
  private String orderNumber;
  private double productPrice;
  private String reason;
  private String modifiedUser;
  private LocalDateTime modifiedOrderdStatusDate;
  private UserTypeDetailsDto userDetails;

 public OrderDetailsDTO() {}

  
}

