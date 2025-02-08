package com.own.project.repository;

import com.own.project.model.Cart;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepo extends  JpaRepository<Cart, Long>{

  List<Cart> findByUser_UserId(Long userId);

  List<Cart> findByUser_UserIdAndCartRemoveStatusNot(Long userId, String cartRemoveStatus);


 Optional<Cart> findByUserAndProduct(UserTypeDetails user, Product product);


  
}
