package com.own.project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.own.project.dao.MainControllerDao;

@RestController
public class MainController {

  private static final Logger log = LoggerFactory.getLogger(MainController.class);

  @Autowired
  private MainControllerDao mainControllerDao;

  @GetMapping("/hello")
  public String hello() {
    log.info("hello");
    return mainControllerDao.hello();
  }

}
