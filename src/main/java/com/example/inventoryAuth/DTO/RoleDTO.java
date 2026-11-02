package com.example.inventoryAuth.DTO;

import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {
    private String code;
    private String name;
    private List<String> permissionCodes;
}
