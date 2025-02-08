package com.own.project.controller;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.own.project.dao.UserTypeDetailsDao;
import com.own.project.dto.LoginRequestDto;
import com.own.project.dto.LoginResponseDto;
import com.own.project.dto.UserTypeDetailsDto;
import com.own.project.exception.UserException;
import com.own.project.model.UserTypeDetails;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.own.project.Config.JwtUtil;
import com.own.project.Config.TokenBlacklist;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/users")
public class UserTypeDetailsController {

  private static final Logger log = LoggerFactory.getLogger(UserTypeDetailsController.class);

  @Autowired
  private UserTypeDetailsDao userTypeDetailsDao;

 
    
  @PostMapping("/login1")
  public ResponseEntity<UserTypeDetails> createUser1(@RequestBody UserTypeDetails userTypeDetails) {
    log.info("In UserTypeDetailsController createUser1()");
    UserTypeDetails savedUser = userTypeDetailsDao.saveUser(userTypeDetails);
    return ResponseEntity.ok(savedUser);
  }

  @PostMapping("/signIn")
  public ResponseEntity<UserTypeDetails> createUser(@ModelAttribute UserTypeDetailsDto userTypeDetailsDto) {
    log.info("In UserTypeDetailsController of createUser()");

    if (userTypeDetailsDao.existsByEmailId(userTypeDetailsDto.getEmailId())) {
      throw new UserException("User  with email " + userTypeDetailsDto.getEmailId() + " already exists.");
    }
    UserTypeDetails userTypeDetails = userTypeDetailsDao.convertDtoToEntity(userTypeDetailsDto);
    if (userTypeDetailsDto.getProfileImage() != null && !userTypeDetailsDto.getProfileImage().isEmpty()) {
      MultipartFile profileImage = userTypeDetailsDto.getProfileImage();
      byte[] imageBytes = convertImageToBytes(profileImage);
      userTypeDetails.setProfileImage(imageBytes);
    } else {
      userTypeDetails.setProfileImage(null); // or set to a default byte array
    }
    UserTypeDetails savedUser = userTypeDetailsDao.saveUser(userTypeDetails);
    return ResponseEntity.ok(savedUser);
  }

  private byte[] convertImageToBytes(MultipartFile profileImage) {
    log.info("In UserTypeDetailsController of convertImageToBytes()");
    try {
      return profileImage.getBytes();
    } catch (IOException e) {
      log.error("Error converting image to bytes", e);
      return null;
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest,HttpServletResponse response,HttpServletRequest request) {
    log.info("In UserTypeDetailsController of login()");
    boolean isLoginSuccessful = userTypeDetailsDao.login(loginRequest.getEmailId(), loginRequest.getUserPassword());

    if (isLoginSuccessful) {
      Optional<UserTypeDetails> user = userTypeDetailsDao.getUserByEmail(loginRequest.getEmailId());

      if (user.isPresent()) {
      
        String userRole = user.get().getUserRole();
        Long userId =user.get().getUserId();
        String token = JwtUtil.generateToken(user.get().getEmailId(), userRole,userId);
        
        return ResponseEntity.ok(new LoginResponseDto("Login Success",token));
        
        //return ResponseEntity.ok(new LoginResponseDto("Login Success", user.get().getUserId(),user.get().getUserRole()));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
      }
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }
  }


  @PostMapping("/check-email")
  public ResponseEntity<Boolean> checkEmail(@RequestBody LoginRequestDto emailId) {
    log.info("In UserTypeDetailsController of checkEmail()");
    try {
      boolean exists = userTypeDetailsDao.existsByEmailId(emailId.getEmailId());
      if (exists) {
        return ResponseEntity.ok(true);
      } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
      }
    } catch (Exception e) {
      log.error("Error checking email", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
    }
  }

  // Endpoint to reset the password
  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestBody LoginRequestDto request) {
    log.info("In UserTypeDetailsController of resetPassword()");
    Optional<UserTypeDetails> user = userTypeDetailsDao.getUserByEmail(request.getEmailId());

    if (userTypeDetailsDao.existsByEmailId(request.getEmailId())) {
      userTypeDetailsDao.resetPassword(request.getEmailId(), request.getUserPassword());
      if (user.isPresent()) {
        return ResponseEntity.ok(new LoginResponseDto("Password updated successfully", user.get().getUserId()));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
      }
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email not found");
    }
  }

  // Endpoint to send the reset link if the email is valid
  @PostMapping("/send-reset-link")
  public String sendResetLink(@RequestBody LoginRequestDto emailId) {
    log.info("In UserTypeDetailsController of sendResetLink()");
    if (userTypeDetailsDao.existsByEmailId(emailId.getEmailId())) {
      userTypeDetailsDao.sendResetLink(emailId.getEmailId());
      return "Reset link sent successfully";
    } else {
      return "Email not found";
    }
  }

  @GetMapping("/all-users")
  public ResponseEntity<?> getAllUsers() {
    log.info("In UserTypeDetailsController of getAllUsers()");

    return ResponseEntity.ok(userTypeDetailsDao.getAllUsers());
  }

  @PutMapping("/updateRole")
  public ResponseEntity<?> updateRole(@RequestBody UserTypeDetails user) {
    log.info("In UserTypeDetailsController of updateRole()");
    boolean isUpdated = userTypeDetailsDao.updateUserRole(user.getUserId(), user.getUserRole());
    if (isUpdated) {
      return ResponseEntity.ok(new LoginResponseDto("UserRole updated successfully "));
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to update UserRole");
    }
  }

  @GetMapping("/getUser")
  public ResponseEntity<?> getUserProfile(@RequestParam(name = "userId", required = false, defaultValue = "0") Long userId) {
    log.info("In UserTypeDetailsController of getUserProfile()");
    if (userId == 0) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not exists");
    }
    UserTypeDetails user = userTypeDetailsDao.getUserById(userId);
    return ResponseEntity.ok(user);
  }

 @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
      log.info("In UserTypeDetailsController of logout()");

      // Extract the token from the Authorization header
      String token = request.getHeader("Authorization");

      if (token != null && token.startsWith("Bearer ")) {
          token = token.substring(7);  // Remove "Bearer " part
      } else {
          return ResponseEntity.status(400).body("Invalid token format.");
      }

      // Blacklist the token (mark it as invalid)
      TokenBlacklist.blacklistToken(token);

      // Optionally log out on the server-side
      // You could clear any server-side session if you use them (not required for stateless JWT).

      log.info("Token has been blacklisted: {}", token);

      return ResponseEntity.ok("Logged out successfully");
  }
  
  

}