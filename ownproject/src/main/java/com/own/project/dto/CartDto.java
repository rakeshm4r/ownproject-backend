package com.own.project.dto;

import com.own.project.model.Cart;

import lombok.Data;

@Data
public class CartDto {

  private String productName;
  private Long userId;

  private Long productId;  
  private String productImageBase64;
  private double productPrice;
  private String userName;
  private String cartRemoveStatus;
  private Long cartId;


  public CartDto(Cart cart){
    this.productName = cart.getProduct().getProductName();
    this.productId = cart.getProduct().getProductId();  // Add the product ID
    this.productImageBase64 = cart.getProduct().getProductImageBase64();  // Add the product image
    this.productPrice = cart.getProduct().getProductPrice(); 
    this.cartRemoveStatus = cart.getCartRemoveStatus();
    this.cartId=cart.getCartId();
}



 

  
  
}
