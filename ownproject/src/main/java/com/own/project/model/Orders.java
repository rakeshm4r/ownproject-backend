package com.own.project.model;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Orders {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int orderId;


  @ManyToOne(fetch = FetchType.LAZY)  
  @JoinColumn(name = "productId", referencedColumnName = "productId")
  private Product product;
  

  @ManyToOne(fetch = FetchType.LAZY)  
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  private UserTypeDetails user;


  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ordersStatusId",referencedColumnName = "ordersStatusId")           
  private OrdersStatus orderStatus;

  
  private String razorpayOrderId;

}
