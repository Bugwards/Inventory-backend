package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.UserDetailsRequest;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Role;
import com.example.inventoryAuth.Entity.User;
import com.example.inventoryAuth.Repository.LocationRepository;
import com.example.inventoryAuth.Repository.RoleRepository;
import com.example.inventoryAuth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                     RoleRepository roleRepository, LocationRepository locationRepository){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
        this.roleRepository=roleRepository;
        this.locationRepository=locationRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    public void saveUser(UserDetailsRequest userDetails){

        Role role = roleRepository.findById(userDetails.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if ("SYSTEM_ADMIN".equals(role.getCode())) {
            throw new IllegalArgumentException("Self-registration as System Admin is not allowed");
        }

        Location location = locationRepository.findById(userDetails.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        User user = new User();
        user.setUsername(userDetails.getUsername());
        user.setRole(role);
        user.setEmail(userDetails.getEmail());
        user.setLocation(location);
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        userRepository.save(user);

    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User updateUserProfile(
            String currentUsername,
            String newUsername,
            String email,
            String phone

    ) {

        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        user.setUsername(newUsername);
        user.setEmail(email);
        user.setPhone(phone);


        return userRepository.save(user);
    }

    public void changePassword(
            String username,
            String currentPassword,
            String newPassword
    ) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }


}
