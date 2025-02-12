package com.own.project.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
public class OrdersStatus {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ordersStatusId;

  private String deliverdStatus;

  private Instant bookedOrderdDate;

  private LocalDateTime deliverdOrderdDate;

  @ManyToOne(fetch = FetchType.LAZY) // Lazy loading can be used to load the category only when needed
  @JoinColumn(name = "productId", referencedColumnName = "productId")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY) // Lazy loading can be used to load the category only when needed
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  private UserTypeDetails user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orderId", referencedColumnName = "orderId")
  private Orders orders;

  @ManyToOne(fetch = FetchType.LAZY) // Lazy loading can be used to load the category only when needed
  @JoinColumn(name = "paymentId", referencedColumnName = "paymentId")
  private PaymentDetails payment;
  

  public void setBookedOrderdDate(Instant bookedOrderdDate) {
    this.bookedOrderdDate = bookedOrderdDate;
    // After setting bookedOrderdDate, calculate and set deliverdOrderdDate
    this.deliverdOrderdDate = calculateDeliverdOrderdDate(bookedOrderdDate);
  }

  public LocalDateTime getDeliverdOrderdDate() {
    return deliverdOrderdDate;
  }

  public void setDeliverdOrderdDate(LocalDateTime deliverdOrderdDate) {
    this.deliverdOrderdDate = deliverdOrderdDate;
  }

  // Method to calculate deliverdOrderdDate (2 days after bookedOrderdDate)
  private LocalDateTime calculateDeliverdOrderdDate(Instant bookedOrderdDate) {
    if (bookedOrderdDate == null) {
      return null; // Handle case when bookedOrderdDate is null
    }
    // Add 2 days to the bookedOrderdDate and convert it to LocalDateTime
    Instant deliveryDateInstant = bookedOrderdDate.plusSeconds(2 * 24 * 60 * 60); // 2 days in seconds
    return LocalDateTime.ofInstant(deliveryDateInstant, ZoneOffset.ofHoursMinutes(5, 30)); //set in IST(+5:30) Time Zone
  }

}
