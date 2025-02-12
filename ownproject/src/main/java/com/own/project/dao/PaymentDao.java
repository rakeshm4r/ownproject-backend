package com.own.project.dao;

import com.own.project.dto.PaymentRequest;
import com.own.project.model.PaymentDetails;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;

public interface PaymentDao {
  
  public PaymentDetails savePayment(PaymentDetails payment);

  public boolean saveOrderPaymentDetails(PaymentRequest paymentRequest, UserTypeDetails user, Product product);


}
