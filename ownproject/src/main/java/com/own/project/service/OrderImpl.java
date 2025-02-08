package com.own.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.own.project.dao.OrderDao;
import com.own.project.model.Orders;
import com.own.project.repository.OrderRepo;


@Service
public class OrderImpl implements OrderDao {

   private static final Logger log = LoggerFactory.getLogger(OrderImpl.class);

    @Autowired private OrderRepo orderRepo;

   @Override
   public Orders saveOrders(Orders orders) {
      log.info("In OrderImpl of saveOrders()");
         return orderRepo.save(orders);
     
   }
   
  
}
