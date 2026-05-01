package com.example.inventoryAuth.Controller;



import com.example.inventoryAuth.DTO.AuthRequest;
import com.example.inventoryAuth.DTO.UserDetailsRequest;
import com.example.inventoryAuth.Repository.UserRepository;
import com.example.inventoryAuth.Service.CustomUserDetailsService;
import com.example.inventoryAuth.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;


    @PostMapping("/register")
    public String userLogin(@RequestBody UserDetailsRequest user){
        customUserDetailsService.saveUser(user);
        return "User Added Successfully";
    }


    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest){

        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword())
            );

            return jwtUtil.generateToken(authRequest.getUsername(),userRepository.findByUsername(authRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found")).getRole());

        }
        catch (Exception e){
            return "invalid Credential";
        }
    }
}
