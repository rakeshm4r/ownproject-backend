package com.own.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.own.project.dao.InvoiceDao;
import com.own.project.model.OrdersStatus;
import com.own.project.repository.OrdersStatusRepo;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/invoice")
public class InvoiceController {
  
  private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

  @Autowired private OrdersStatusRepo ordersStatusRepo;

  @Autowired private InvoiceDao invoiceDao;



  @GetMapping(value = "/{ordersStatusId}", produces = MediaType.APPLICATION_PDF_VALUE)
  @CrossOrigin(origins = "http://localhost:4200", exposedHeaders = "Content-Disposition") // Add this line
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long ordersStatusId) {
      log.info("In InvoiceController of generateInvoice()");
        try {
            OrdersStatus orderStatus = ordersStatusRepo.findById(ordersStatusId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
                    
            byte[] pdfBytes = invoiceDao.generateInvoice(orderStatus);

            String invoiceNumber = orderStatus.getInvoiceDetails().getInvoiceNumber();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                                                            .filename(invoiceNumber + ".pdf")
                                                            .build()); // Correct setting of the Content-Disposition header

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
