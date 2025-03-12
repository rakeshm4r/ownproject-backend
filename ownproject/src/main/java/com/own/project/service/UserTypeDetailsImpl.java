package com.own.project.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.own.project.dao.UserTypeDetailsDao;
import com.own.project.dto.UserTypeDetailsDto;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.UserTypeDetailsRepo;

@Service
public class UserTypeDetailsImpl implements UserTypeDetailsDao {

  private static final Logger log = LoggerFactory.getLogger(UserTypeDetailsImpl.class);

  @Autowired
  private UserTypeDetailsRepo userTypeDetailsRepo;

  @Override
  public UserTypeDetails convertDtoToEntity(UserTypeDetailsDto userTypeDetailsDto) {
    log.info("In UserTypeDetailsImpl convertDtoToEntity()");
    UserTypeDetails userTypeDetails = new UserTypeDetails();
    userTypeDetails.setUserName(userTypeDetailsDto.getUserName());
    userTypeDetails.setEmailId(userTypeDetailsDto.getEmailId());
    userTypeDetails.setUserPassword(userTypeDetailsDto.getUserPassword());
    userTypeDetails.setAddress(userTypeDetailsDto.getAddress());
    userTypeDetails.setCity(userTypeDetailsDto.getCity());
    userTypeDetails.setState(userTypeDetailsDto.getState());
    userTypeDetails.setCountry(userTypeDetailsDto.getCountry());
    userTypeDetails.setMobileNo(userTypeDetailsDto.getMobileNo());
    userTypeDetails.setPinCode(userTypeDetailsDto.getPinCode());

    // Convert MultipartFile to byte array
    MultipartFile profileImage = userTypeDetailsDto.getProfileImage();
    if (profileImage != null && !profileImage.isEmpty()) {
      try {
        userTypeDetails.setProfileImage(profileImage.getBytes());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return userTypeDetails;
  }

  @Override
  public UserTypeDetails saveUser(UserTypeDetails userTypeDetails) {
    log.info("In UserTypeDetailsImpl saveUser()");
    userTypeDetails.setUserLoginDate(LocalDateTime.now().withNano(0));
    userTypeDetails.setUserStatus("0");
    userTypeDetails.setUserRole("User");
    return userTypeDetailsRepo.save(userTypeDetails);
  }

  @Override
  public boolean existsByEmailId(String emailId) {
    log.info("In UserTypeDetailsImpl existsByEmailId()");
    return userTypeDetailsRepo.existsByEmailId(emailId);
  }

  public boolean login(String emailId, String password) {
    log.info("In UserTypeDetailsImpl login()");
    Optional<UserTypeDetails> user = userTypeDetailsRepo.findByEmailId(emailId);

    if (user.get().getEmailId().equals(emailId) && user.get().getUserPassword().equals(password)) {
      return true;
    }
    return false;
  }

  @Override
  public Optional<UserTypeDetails> getUserByEmail(String emailId) {
    log.info("In UserTypeDetailsImpl getUserByEmail()");
    return userTypeDetailsRepo.findByEmailId(emailId);
  }

  // Method to send a password reset email (placeholder logic)
  public void sendResetLink(String emailId) {
    log.info("In UserTypeDetailsImpl sendResetLink()");
  }

  // Method to reset the password in the database
  public void resetPassword(String emailId, String newPassword) {
    log.info("In UserTypeDetailsImpl resetPassword()");
    Optional<UserTypeDetails> user = userTypeDetailsRepo.findByEmailId(emailId);
    if (user.isPresent()) {
      UserTypeDetails userDetails = user.get();
      userDetails.setUserPassword(newPassword);
      userTypeDetailsRepo.save(userDetails);
    }
  }

  @Override
  public List<UserTypeDetails> getAllUsers() {
    log.info("In UserTypeDetailsImpl getAllUsers()");

    return userTypeDetailsRepo.findAll();
  }

  public boolean updateUserRole(Long userId, String newRole) {
    log.info("In UserTypeDetailsImpl updateUserRole()");
    UserTypeDetails user = userTypeDetailsRepo.findById(userId).orElse(null);
    if (user != null) {
      user.setUserRole(newRole);
      userTypeDetailsRepo.save(user);
      return true;
    }
    return false;
  }

  @Override
  public UserTypeDetails getUserById(Long userId) {
    log.info("In UserTypeDetailsImpl getUserById()");
    return userTypeDetailsRepo.findById(userId).orElse(null);
  }

  public UserTypeDetailsDto mapUserToUserTypeDetailsDto(UserTypeDetails user) {
    UserTypeDetailsDto userTypeDetailsDto = new UserTypeDetailsDto();
    userTypeDetailsDto.setUserName(user.getUserName());
    userTypeDetailsDto.setEmailId(user.getEmailId());
    userTypeDetailsDto.setMobileNo(user.getMobileNo());
    userTypeDetailsDto.setAddress(user.getAddress());
    userTypeDetailsDto.setCity(user.getCity());
    userTypeDetailsDto.setState(user.getState());
    userTypeDetailsDto.setPinCode(user.getPinCode());
    userTypeDetailsDto.setCountry(user.getCountry());
    return userTypeDetailsDto;
  }
  
}
