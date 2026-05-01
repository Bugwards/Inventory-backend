package com.example.inventoryAuth.Service;

<<<<<<< HEAD
=======


>>>>>>> aae77b0cf1d385ca1513e1d4cf8901adc6e1ea1b
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
<<<<<<< HEAD
        User user = new User();
        user.setUsername(userDetails.getUsername());
        user.setRole(userDetails.getRole());
        user.setEmail(userDetails.getEmail());
        user.setLocation(userDetails.getLocation());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        userRepository.save(user);
=======
          User user = new User();
          user.setUsername(userDetails.getUsername());
          user.setRole(userDetails.getRole());
          user.setEmail(userDetails.getEmail());
          user.setLocation(userDetails.getLocation());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
         userRepository.save(user);
>>>>>>> aae77b0cf1d385ca1513e1d4cf8901adc6e1ea1b
    }


}
