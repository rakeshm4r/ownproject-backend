package com.own.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.own.project.model.OrdersDetails;
import com.own.project.model.PaymentDetails;

@Repository
public interface PaymentRepo extends JpaRepository <PaymentDetails, Long>{
   PaymentDetails findByOrders(OrdersDetails order);
}
