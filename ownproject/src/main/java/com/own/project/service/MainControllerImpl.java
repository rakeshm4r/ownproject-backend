package com.own.project.service;

import org.springframework.stereotype.Service;

import com.own.project.dao.MainControllerDao;

@Service
public class MainControllerImpl implements MainControllerDao {

  @Override
  public String hello() {
    return "hello";
  }

}
