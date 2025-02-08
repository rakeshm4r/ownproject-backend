package com.own.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.own.project.model.UserTypeDetails;

@Repository
public interface UserTypeDetailsRepo extends JpaRepository<UserTypeDetails, Long> {
   
  boolean existsByEmailId(String emailId);

  Optional<UserTypeDetails> findByEmailId(String emailId);

  
  
}
