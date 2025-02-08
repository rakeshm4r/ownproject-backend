package com.own.project.dto;



import lombok.Data;

@Data
public class LoginResponseDto {
  
  
  private String message;
  private Long userId;
  private String userRole;
  private String token;

  
  public LoginResponseDto(String message) { 
    this.message = message;
  }

  public LoginResponseDto(String message, Long userId) {
    this.message = message;
    this.userId = userId;
  }

  public LoginResponseDto(String message, String token) {
    this.message = message;
    this.token = token;
  }

  public LoginResponseDto(String message, Long userId,String userRole) {
    this.message = message;
    this.userId = userId;
    this.userRole=userRole;
  }

  public LoginResponseDto(String message, Long userId, String userRole, String token) {
    this.message = message;
    this.userId = userId;
    this.userRole = userRole;
    this.token = token;
}
}
