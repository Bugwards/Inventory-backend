package com.example.inventoryAuth.DTO;


import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsRequest {

    private String username;

    private String password;

    private String email;

    private Location location;

    private Role role;


}

