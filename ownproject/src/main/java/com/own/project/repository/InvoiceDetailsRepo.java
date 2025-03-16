package com.own.project.repository;

import com.own.project.model.InvoiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDetailsRepo  extends JpaRepository <InvoiceDetails, Integer>{

  @Query("SELECT MAX(CAST(SUBSTRING(inv.invoiceNumber, 10,6) AS int)) FROM InvoiceDetails inv WHERE inv.invoiceNumber LIKE :invPrefix%")
   public Integer findMaxInvoiceNumber(@Param("invPrefix") String datePrefix);
  
}
