package com.example.inventoryAuth.Controller;

import com.example.inventoryAuth.Entity.Permission;
import com.example.inventoryAuth.Repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    PermissionRepository permissionRepository;

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @GetMapping
    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }
}
