package com.own.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.own.project.dao.OrdersStatusDao;
import com.own.project.model.OrdersStatus;
import com.own.project.repository.OrdersStatusRepo;


@Service
public class OrdersStatusImpl  implements OrdersStatusDao{

  private static final Logger log = LoggerFactory.getLogger(OrdersStatusImpl.class);

  @Autowired private OrdersStatusRepo ordersStatusRepo;

   public OrdersStatus savOrdersStatus(OrdersStatus ordersStatus){
    log.info("In OrdersStatusImpl of savOrdersStatus()");
    return ordersStatusRepo.save(ordersStatus);
   }
}
