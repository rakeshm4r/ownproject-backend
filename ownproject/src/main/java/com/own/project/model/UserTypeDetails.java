package com.own.project.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class UserTypeDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true)
  private String emailId;

  private String userName;
  private String userPassword;
  private String userRole;
  private String userStatus;
  private LocalDateTime userLoginDate;
  private String mobileNo;
  private String address;
  private String city;
  private String state;
  private String pinCode;
  private String country;
  private byte[] profileImage;



}
