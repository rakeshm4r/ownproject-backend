package com.own.project.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import lombok.Data;

@Entity
@Data
public class InvoiceDetails {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int invoiceId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", referencedColumnName = "userId")
  private UserTypeDetails user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ordersStatusId", referencedColumnName = "ordersStatusId")
  private OrdersStatus orderStatus;

  @Column(unique = true)
  private String invoiceNumber;
}
