package com.own.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.own.project.dao.PaymentDao;
import com.own.project.model.Payment;
import com.own.project.repository.PaymentRepo;


@Service
public class PaymentImpl  implements PaymentDao{

  private static final Logger log = LoggerFactory.getLogger(PaymentImpl.class);

  @Autowired private PaymentRepo paymentRepo;

  public Payment savePayment(Payment payment){
    log.info("In PaymentImpl of savePayment()");
    return paymentRepo.save(payment);
  } 
  
}
