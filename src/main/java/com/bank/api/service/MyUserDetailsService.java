package com.bank.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bank.api.model.User;
import com.bank.api.repository.UserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("This user does not exist");
        }

        User user = userOptional.get();

        // Use authorities() since we store "ROLE_USER" with prefix
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole())
                .build();
    }

}
