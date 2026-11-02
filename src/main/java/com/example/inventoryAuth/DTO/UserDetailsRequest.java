package com.example.inventoryAuth.DTO;


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

    private Long locationId;

    private Long roleId;


}

