package com.own.project.Resposes;

import lombok.Data;

@Data
public class ApiResponse {

  
  private String message;
  private boolean success;
  private String value;

  public ApiResponse(String message){
    this.message = message;
  }

 
  public ApiResponse(String message, boolean success) {
      this.message = message;
      this.success = success;
  }

  public ApiResponse(String message, boolean success, String value) {
    this.message = message;
    this.success = success;
    this.value = value;
}

 
}

