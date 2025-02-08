package com.own.project.service;



import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.own.project.dao.UserTypeDetailsDao;
import com.own.project.model.UserTypeDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserTypeDetailsDao userTypeDetailsDao; // Inject your DAO for database access

    public CustomUserDetailsService(UserTypeDetailsDao userTypeDetailsDao) {
        this.userTypeDetailsDao = userTypeDetailsDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Here you load the user by username from your database
        // For example, get the user from the userTypeDetailsDao

        Optional<UserTypeDetails> user = userTypeDetailsDao.getUserByEmail(username);

        if (user.isPresent()) {
            // You can customize the authorities based on the user roles
            return User.builder()
                       .username(user.get().getEmailId())
                       .password(user.get().getUserPassword())
                       .roles(user.get().getUserRole()) // This assumes that the role is stored in a single string, update as necessary
                       .build();
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}

