package com.own.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.own.project.model.Orders;


@Repository
public interface OrderRepo   extends JpaRepository <Orders, String>{
  
}
