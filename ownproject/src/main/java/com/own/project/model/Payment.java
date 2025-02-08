package com.own.project.model;


import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentId;

	private LocalDateTime paymentDate;	

	private int paymentAmount;

  private String paymentTypeName;

  private String paymentStatus;
	
	@ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  private UserTypeDetails user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orderId", referencedColumnName = "orderId")
  private Orders orders;

  @OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ordersStatusId",referencedColumnName = "ordersStatusId")           
  private OrdersStatus orderStatus;

  @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading can be used to load the category only when needed
  @JoinColumn(name = "productId", referencedColumnName = "productId")
  private Product product;

}
