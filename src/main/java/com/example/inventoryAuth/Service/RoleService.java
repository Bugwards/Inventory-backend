package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.RoleDTO;
import com.example.inventoryAuth.DTO.RoleSummaryDTO;
import com.example.inventoryAuth.Entity.Permission;
import com.example.inventoryAuth.Entity.Role;
import com.example.inventoryAuth.Repository.PermissionRepository;
import com.example.inventoryAuth.Repository.RoleRepository;
import com.example.inventoryAuth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private static final String SYSTEM_MANAGEMENT_PERMISSION = "SYSTEM_MANAGEMENT_MANAGE";

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    public List<RoleSummaryDTO> getSummary() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleSummaryDTO(role.getId(), role.getCode(), role.getName()))
                .toList();
    }

    public Role create(RoleDTO dto) {
        if (roleRepository.findByCode(dto.getCode()).isPresent()) {
            throw new RuntimeException("Role code already exists: " + dto.getCode());
        }
        Role role = new Role();
        role.setCode(dto.getCode());
        role.setName(dto.getName());
        role.setSystemDefined(false);
        role.setPermissions(resolvePermissions(dto.getPermissionCodes()));
        return roleRepository.save(role);
    }

    public Role update(Long id, RoleDTO dto) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (existing.getSystemDefined() && dto.getCode() != null && !dto.getCode().equals(existing.getCode())) {
            throw new RuntimeException("Cannot change the code of a system-defined role");
        }

        Set<Permission> newPermissions = resolvePermissions(dto.getPermissionCodes());
        boolean losesSystemManagement = existing.getPermissions().stream()
                .anyMatch(p -> p.getCode().equals(SYSTEM_MANAGEMENT_PERMISSION))
                && newPermissions.stream().noneMatch(p -> p.getCode().equals(SYSTEM_MANAGEMENT_PERMISSION));

        if (losesSystemManagement && countRolesWithSystemManagement() <= 1) {
            throw new RuntimeException("Cannot remove System Management access from the last role that has it");
        }

        if (!existing.getSystemDefined() && dto.getCode() != null) {
            existing.setCode(dto.getCode());
        }
        existing.setName(dto.getName());
        existing.setPermissions(newPermissions);
        return roleRepository.save(existing);
    }

    public void delete(Long id) {
        Role existing = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        if (existing.getSystemDefined()) {
            throw new RuntimeException("Cannot delete a system-defined role");
        }
        if (userRepository.existsByRole_Id(id)) {
            throw new RuntimeException("Cannot delete a role that is still assigned to users");
        }
        roleRepository.delete(existing);
    }

    private long countRolesWithSystemManagement() {
        return roleRepository.findAll().stream()
                .filter(r -> r.getPermissions().stream().anyMatch(p -> p.getCode().equals(SYSTEM_MANAGEMENT_PERMISSION)))
                .count();
    }

    private Set<Permission> resolvePermissions(List<String> codes) {
        Set<Permission> permissions = new HashSet<>();
        if (codes == null) {
            return permissions;
        }
        for (String code : codes) {
            Permission permission = permissionRepository.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Unknown permission code: " + code));
            permissions.add(permission);
        }
        return permissions;
    }
}
