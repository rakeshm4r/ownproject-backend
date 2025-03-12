package com.own.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.own.project.model.OrdersDetails;
import com.own.project.model.OrdersStatus;


@Repository
public interface OrdersStatusRepo   extends JpaRepository <OrdersStatus, Long>{
   OrdersStatus findByOrders(OrdersDetails order);

   @Query("SELECT os FROM OrdersStatus os WHERE os.deliverdStatus = 'packed&shipped'")
    List<OrdersStatus> findPackedAndShippedOrders();
}
