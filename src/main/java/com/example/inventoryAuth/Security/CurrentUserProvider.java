package com.example.inventoryAuth.Security;

import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.User;
import com.example.inventoryAuth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    @Autowired
    UserRepository userRepository;

    public String getCurrentUsername() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }

    public Location getCurrentUserLocation() {
        return getCurrentUser().getLocation();
    }

    public User getCurrentUser() {

        return userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
