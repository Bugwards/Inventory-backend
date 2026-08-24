package com.example.inventoryAuth.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {

    private String currentUsername;
    private String newUsername;
    private String email;
    private String phone;

}