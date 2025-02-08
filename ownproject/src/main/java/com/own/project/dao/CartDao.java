package com.own.project.dao;

import java.util.List;
import com.own.project.model.Cart;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;

public interface CartDao {


 // public boolean addToCart(CartDto cartItemDTO);

  public List<Cart> getCartDetailsByUserId(Long userId);

  public boolean addToCart(Product product, UserTypeDetails user);

  public boolean removeProductFromCart(Long cartId);
  
 
}
