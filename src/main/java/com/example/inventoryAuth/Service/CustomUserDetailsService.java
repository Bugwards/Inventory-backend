package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.UserDetailsRequest;
import com.example.inventoryAuth.Entity.User;
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

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository , PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    public void saveUser(UserDetailsRequest userDetails){

        User user = new User();
        user.setUsername(userDetails.getUsername());
        user.setRole(userDetails.getRole());
        user.setEmail(userDetails.getEmail());
        user.setDepartment(userDetails.getDepartment());
        user.setName(userDetails.getName());
        user.setLocation(userDetails.getLocation());
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
