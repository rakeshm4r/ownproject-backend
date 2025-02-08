package com.own.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.own.project.model.Payment;

@Repository
public interface PaymentRepo extends JpaRepository <Payment, Long>{
  
}
