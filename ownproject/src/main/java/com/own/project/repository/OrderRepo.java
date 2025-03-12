package com.own.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.own.project.model.OrdersDetails;
import com.own.project.model.UserTypeDetails;


@Repository
public interface OrderRepo   extends JpaRepository <OrdersDetails, Integer>{
   List<OrdersDetails> findByUser(UserTypeDetails user);

   @Query("SELECT MAX(CAST(SUBSTRING(o.orderNumber, 10,6) AS int)) FROM OrdersDetails o WHERE o.orderNumber LIKE :orderPrefix%")
public Integer findMaxOrderNumber(@Param("orderPrefix") String datePrefix);



}
