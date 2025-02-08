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
public class Cart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long cartId;


  @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading can be used to load the category only when needed
  @JoinColumn(name = "productId", referencedColumnName = "productId")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading can be used to load the category only when needed
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  private UserTypeDetails user;

  private String cartRemoveStatus; 
  
  
}
