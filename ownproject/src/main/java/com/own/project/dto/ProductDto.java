package com.own.project.dto;

import com.own.project.model.Product;

import lombok.Data;

@Data
public class ProductDto {
  private Long productId; 
  private String productName;
  private double productPrice;
  private int noOfItems;
  private String productImageBase64;
  private String categoryName;

  public ProductDto(Product product) {
      this.productId=product.getProductId();
      this.productName = product.getProductName();
      this.productPrice = product.getProductPrice();
      this.noOfItems = product.getNoOfItems();
      this.productImageBase64 = product.getProductImageBase64();
      if (product.getProductCategory() != null) {
          this.categoryName = product.getProductCategory().getProductCategoryName(); 
      }
  }

  
}

