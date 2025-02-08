package com.own.project.dto;

import com.own.project.model.Product;

import lombok.Data;

@Data
public class ProductUserDto {
  private Long productId; 
  private String productName;
  private double productPrice;
  private String productImageBase64;

  public ProductUserDto(Product product) {
      this.productId=product.getProductId();
      this.productName = product.getProductName();
      this.productPrice = product.getProductPrice();
      this.productImageBase64 = product.getProductImageBase64();
  }

 
}

