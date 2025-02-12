package com.own.project.exception;

import com.razorpay.RazorpayException;

public class PaymentException extends RuntimeException {

  public PaymentException(String message) {
    super(message);
  }

  public PaymentException(String string, RazorpayException e) {
    
  }
  
}
