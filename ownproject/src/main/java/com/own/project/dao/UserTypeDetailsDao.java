package com.own.project.dao;

import java.util.List;
import java.util.Optional;

import com.own.project.dto.UserTypeDetailsDto;
import com.own.project.model.UserTypeDetails;

public interface UserTypeDetailsDao {

  public UserTypeDetails saveUser(UserTypeDetails userTypeDetails);

  public UserTypeDetails convertDtoToEntity(UserTypeDetailsDto userTypeDetailsDto);

  public  boolean existsByEmailId(String emailId);

  public boolean login(String emailId, String password);

  public Optional<UserTypeDetails> getUserByEmail(String emailId);  

  public void sendResetLink(String emailId);

  public void resetPassword(String emailId, String newPassword);

  public List<UserTypeDetails> getAllUsers();

  public boolean updateUserRole(Long userId, String role);

  public UserTypeDetails getUserById(Long userId);
  
}
