package com.own.project.dto;

import java.util.List;

import lombok.Data;

@Data
public class PaymentRequest {
  private List<ProductItem> products; // New: list of products with quantity
  private double amount;
  private String  razorpay_payment_id;
  private String razorpay_order_id;
  private String razorpay_signature;
  private Long productId;
  private int quantity;
  
  // Inner static class for product item
  public static class ProductItem {
      private Long productId;
      private int quantity;
      public Long getProductId() {
        return productId;
      }
      public void setProductId(Long productId) {
        this.productId = productId;
      }
      public int getQuantity() {
        return quantity;
      }
      public void setQuantity(int quantity) {
        this.quantity = quantity;
      }
  }
}

