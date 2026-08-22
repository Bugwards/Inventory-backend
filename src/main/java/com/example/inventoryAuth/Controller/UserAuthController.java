package com.example.inventoryAuth.Controller;

import com.example.inventoryAuth.DTO.*;
import com.example.inventoryAuth.Entity.User;

//for pro pic
import com.example.inventoryAuth.Repository.UserRepository;
import com.example.inventoryAuth.Service.CustomUserDetailsService;
import com.example.inventoryAuth.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


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
    public String register(@RequestBody UserDetailsRequest user) {
        customUserDetailsService.saveUser(user);
        return "User Added Successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            return jwtUtil.generateToken(authRequest.getUsername(),
                    userRepository.findByUsername(authRequest.getUsername())
                            .orElseThrow(() -> new UsernameNotFoundException("User not found")).getRole());

        } catch (Exception e) {
            return "invalid Credential";
        }
    }
    //newly added
    @GetMapping("/profile")
    public UserProfileResponse getProfile(
            @RequestParam String username
    ) {

        User user =
                customUserDetailsService.getUserByUsername(username);


        return new UserProfileResponse(
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getLocation(),
                user.getRole()
        );
    }

    @PutMapping("/profile")
    public String updateProfile(
            @RequestBody UserProfileUpdateRequest request
    ) {

        customUserDetailsService.updateUserProfile(
                request.getCurrentUsername(),
                request.getNewUsername(),
                request.getEmail(),
                request.getPhone()

        );

        return "Profile updated successfully";
    }

    @PutMapping("/change-password")
    public String changePassword(
            @RequestBody ChangePasswordRequest request
    ) {

        try {

            customUserDetailsService.changePassword(
                    request.getUsername(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );

            return "Password changed successfully";

        } catch (IllegalArgumentException e) {

            return e.getMessage();
        }
    }

    // PROFILE PICTURE UPLOAD

    @PutMapping(
            value = "/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> uploadProfilePicture(
            @RequestParam("username") String username,
            @RequestPart("file") MultipartFile file
    ) {

        try {

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Please select an image.");
            }

            // Check that the uploaded file is actually an image
            String contentType = file.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                        .body("Please select a valid image file.");
            }

            User user =
                    customUserDetailsService.getUserByUsername(username);

            user.setProfilePicture(file.getBytes());

            userRepository.save(user);

            return ResponseEntity.ok(
                    "Profile picture updated successfully."
            );

        } catch (IOException e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body("Failed to read the image file.");

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body("Failed to save profile picture: " + e.getMessage());
        }
    }


    // GET PROFILE PICTURE

    @GetMapping("/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(
            @RequestParam("username") String username
    ) {

        try {

            User user =
                    customUserDetailsService.getUserByUsername(username);

            byte[] image = user.getProfilePicture();

            if (image == null || image.length == 0) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(image);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        }
    }

}
