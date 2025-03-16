package com.own.project.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.persistence.CascadeType;
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

    private LocalDateTime bookedOrderdDate;

    private LocalDateTime deliverdOrderdDate;

    private Long modifiedUserByOrdStatus;

    private String reason;

    private int itemsQuantity;
    
    private LocalDateTime modifiedOrderdStatusDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private UserTypeDetails user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private OrdersDetails orders;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "paymentId", referencedColumnName = "paymentId")
    private PaymentDetails payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoiceId", referencedColumnName = "invoiceId")
    private InvoiceDetails invoiceDetails;

    // Method to set bookedOrderdDate directly from LocalDateTime
    public void setBookedOrderdDate(LocalDateTime bookedOrderdDate) {
        if (bookedOrderdDate == null) {
            throw new RuntimeException("Booked order date cannot be null.");
        }
        this.bookedOrderdDate = bookedOrderdDate.withNano(0);
        // After setting bookedOrderdDate, calculate and set deliverdOrderdDate
        this.deliverdOrderdDate = calculateDeliverdOrderdDate(this.bookedOrderdDate);
    }

    public void setDeliverdOrderdDate(String deliverdOrderdDateStr) {
        try {
            this.deliverdOrderdDate = LocalDateTime.parse(deliverdOrderdDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).withNano(0);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date format. Please use 'yyyy-MM-dd HH:mm:ss'.", e);
        }
    }

    public LocalDateTime getDeliverdOrderdDate() {
        return deliverdOrderdDate;
    }

    // Method to calculate deliverdOrderdDate (2 days after bookedOrderdDate)
    private LocalDateTime calculateDeliverdOrderdDate(LocalDateTime bookedOrderdDate) {
        if (bookedOrderdDate == null) {
            return null; // Handle case when bookedOrderdDate is null
        }
        // Add 2 days to the bookedOrderdDate
        return bookedOrderdDate.plusDays(2);
    }

    // Method to get bookedOrderdDate as a formatted string
    public String getFormattedBookedOrderdDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return bookedOrderdDate != null ? bookedOrderdDate.format(formatter) : null;
    }

    // Method to get deliverdOrderdDate as a formatted string
    public String getFormattedDeliverdOrderdDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return deliverdOrderdDate != null ? deliverdOrderdDate.format(formatter) : null;
    }
}