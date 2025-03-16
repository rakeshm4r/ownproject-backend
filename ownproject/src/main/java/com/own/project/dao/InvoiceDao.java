package com.own.project.dao;

import com.own.project.model.OrdersStatus;

public interface InvoiceDao {

  public byte[] generateInvoice(OrdersStatus orderStatus);
  
}
