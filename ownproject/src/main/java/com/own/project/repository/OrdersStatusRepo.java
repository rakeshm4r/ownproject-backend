package com.own.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.own.project.model.OrdersStatus;


@Repository
public interface OrdersStatusRepo   extends JpaRepository <OrdersStatus, Long>{
  
}
