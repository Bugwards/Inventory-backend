package com.example.inventoryAuth.Controller;

import com.example.inventoryAuth.DTO.RoleDTO;
import com.example.inventoryAuth.DTO.RoleSummaryDTO;
import com.example.inventoryAuth.Entity.Role;
import com.example.inventoryAuth.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    RoleService roleService;

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @GetMapping
    public List<Role> getAll() {
        return roleService.getAll();
    }

    @GetMapping("/summary")
    public List<RoleSummaryDTO> getSummary() {
        return roleService.getSummary();
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PostMapping
    public Role create(@RequestBody RoleDTO dto) {
        return roleService.create(dto);
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PutMapping("/{id}")
    public Role update(@PathVariable Long id, @RequestBody RoleDTO dto) {
        return roleService.update(id, dto);
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roleService.delete(id);
    }
}
