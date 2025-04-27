package com.own.project.service;



import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.own.project.dao.CartDao;
import com.own.project.model.Cart;
import com.own.project.model.Product;
import com.own.project.model.UserTypeDetails;
import com.own.project.repository.CartRepo;

@Service
public class CartImpl  implements CartDao{

 @Autowired
    private CartRepo cartRepo;

    private static final Logger log = LoggerFactory.getLogger(CartImpl.class); 
   

    public boolean addToCart(Product product, UserTypeDetails user) {
        log.info("In CartImpl of addToCart()");
    
        Optional<Cart> existingCart = cartRepo.findByUserAndProduct(user, product);
    
        if (existingCart.isPresent()) {
            
            Cart cart = existingCart.get();
            cart.setCartRemoveStatus("0");
            cartRepo.save(cart);
            
            log.info("Product already in cart.So, Cart is updated.");
            return true; 

        } else {
           
            Cart cart = new Cart();
            cart.setProduct(product);
            cart.setUser(user);
            cart.setCartRemoveStatus("0");
            Cart savedCart = cartRepo.save(cart);
            if (savedCart != null) {
                log.info("Product added to cart.");
                return true;
            } else {
                log.error("Failed to add product to cart.");
                return false;
            }
        }
    }
    
    
    public List<Cart> getCartDetailsByUserId(Long userId) {
        log.info("In CartImpl of getCartDetailsByUserId()");    
        
        List<Cart> carts = cartRepo.findByUser_UserIdAndCartRemoveStatusNot(userId, "1");

        return carts;
    }

    public boolean removeProductFromCart(Long cartId) {
        log.info("In CartImpl of removeProductFromCart()");    
        
        Cart cart = cartRepo.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setCartRemoveStatus("1");
             cartRepo.save(cart); 
             return true;
        }

        return false;
    }

    public boolean cartStatus(Product product, UserTypeDetails user) {
        log.info("In CartImpl of cartStatus()");
    
       Optional<Cart> existingCart = cartRepo.findByUserAndProduct(user, product);
    
        if (existingCart.isPresent()) {
            Cart cart = existingCart.get();
        
            // Check if the cartRemoveStatus is not indicating it's removed (or if you need a specific status)
            if (cart.getCartRemoveStatus() == null || !cart.getCartRemoveStatus().equals("1")) {
                return true;  // Return true if the cart is still active or not removed
            }
        } 
        return false;
    }
}
