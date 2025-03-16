package com.own.project.Util_Services;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.own.project.repository.InvoiceDetailsRepo;
import com.own.project.repository.OrderRepo;

@Component
public class NameUtils {

    @Autowired private OrderRepo orderRepo;
    @Autowired private InvoiceDetailsRepo invoiceDetailsRepo;

    // public static String generateOrderNumber() {
    //     // Get the current date in ddmmyy format
    //     SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
    //     String datePart = sdf.format(new Date());

    //     // Generate the order number prefix (ORD) + date (ddmmyy) + incremented number
    //     String orderNumber = "ORD" + datePart + String.format("%03d", orderCounter);

    //     // Increment the counter for the next order
    //     orderCounter++;
        
    //     // Ensure the counter doesn't exceed 1000
    //     if (orderCounter > 1000) {
    //         orderCounter = 1; // Reset the counter if it exceeds 1000
    //     }

    //     return orderNumber;
    // }
    
    // Repository to interact with the OrdersDetails table

    private static final String ORDER_PREFIX = "ORD"; // Prefix for the order number
    private static final String INV_PREFIX ="INV"; // Prefix for the invoice number
    private static final String DATE_FORMAT = "ddMMyy"; // Date format for the order number

    public String generateOrderNumber() {
        // Get the current date in ddMMyy format
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String currentDate = today.format(formatter);
        String datePart = ORDER_PREFIX + currentDate; // This is the prefix + date part (e.g., ORD240325)

        // Get the latest order number for the current date (ddMMyy)
        Integer maxOrderNumber = orderRepo.findMaxOrderNumber(datePart);

        if(maxOrderNumber == null){
            int newOrderNumber = 1;
            String newOrderNumberStr = String.format("%04d", newOrderNumber); 
             return datePart + newOrderNumberStr;
        }

        // Extract the numeric part from the order number (last 3 digits) and increment it
        int newOrderNumber = maxOrderNumber + 1;

        // Format the new order number to have leading zeros if needed (assuming 3 digits)
        String newOrderNumberStr = String.format("%04d", newOrderNumber);        
      
        // Return the new order number
        return datePart + newOrderNumberStr;
    }

    public String generateInvoiceNumber() {
        // Get the current date in ddMMyy format
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String currentDate = today.format(formatter);
        String datePart = INV_PREFIX + currentDate; // This is the prefix + date part (e.g., INV240325)

        // Get the latest order number for the current date (ddMMyy)
        Integer maxInvoiceNumber = invoiceDetailsRepo.findMaxInvoiceNumber(datePart);

        if(maxInvoiceNumber == null){
            int newInvoiceNumber = 1;
            String newInvoiceNumberStr = String.format("%04d", newInvoiceNumber); 
             return datePart + newInvoiceNumberStr;
        }

        // Extract the numeric part from the Invoice number (last 3 digits) and increment it
        int newInvoiceNumber = maxInvoiceNumber + 1;

        // Format the new Invoice number to have leading zeros if needed (assuming 3 digits)
        String newInvoiceNumberStr = String.format("%04d", newInvoiceNumber);        
      
        // Return the new Invoice number
        return datePart + newInvoiceNumberStr;
    }
}

