package com.own.project.model;

import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;


@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(unique = true)  // Ensures that the productName is unique in the database
    private String productName;

    private double productPrice;

    private double discountPrdPrice;

    private int noOfItems;
    
    @ManyToOne(fetch = FetchType.LAZY)  // Lazy loading can be used to load the category only when needed
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    private ProductCategory productCategory;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] productImage;  // Store image as byte array

    // Additional getter for returning image as Base64
    public String getProductImageBase64() {
        if (productImage != null) {
            return Base64.getEncoder().encodeToString(productImage);  // Convert byte[] to base64
        }
        return null;  // If no image, return null
    }

   
}