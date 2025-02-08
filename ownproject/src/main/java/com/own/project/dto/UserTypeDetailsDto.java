package com.own.project.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class UserTypeDetailsDto {
    private String userName;
    private String emailId;
    private String userPassword;
    private String mobileNo; 
    private String address;
    private String city;
    private String state;
    private String pinCode; 
    private String country;

    @Nullable
    private MultipartFile profileImage = null;
}
