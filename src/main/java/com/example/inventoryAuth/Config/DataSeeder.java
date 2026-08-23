package com.example.inventoryAuth.Config;

import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Permission;
import com.example.inventoryAuth.Entity.Role;
import com.example.inventoryAuth.Entity.Supplier;
import com.example.inventoryAuth.Entity.User;
import com.example.inventoryAuth.Repository.LocationRepository;
import com.example.inventoryAuth.Repository.PermissionRepository;
import com.example.inventoryAuth.Repository.RoleRepository;
import com.example.inventoryAuth.Repository.SupplierRepository;
import com.example.inventoryAuth.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dev-only bootstrap: seeds the Permission/Role/Location/Supplier catalog and one
 * SYSTEM_ADMIN user on first startup against an empty database. Idempotent (checks
 * for existing rows), so it is safe to leave running permanently in this environment.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private record PermissionSeed(String code, String description, String category) {
    }

    @Override
    public void run(String... args) {
        Map<String, Permission> permissions = seedPermissions();
        Map<String, Role> roles = seedRoles(permissions);
        seedLocations();
        seedSuppliers();
        seedBootstrapAdmin(roles);
    }

    private Map<String, Permission> seedPermissions() {
        List<PermissionSeed> catalog = List.of(
                new PermissionSeed("GRN_CREATE", "Create/update GRN", "GRN"),
                new PermissionSeed("GRN_APPROVE", "Approve/cancel GRN", "GRN"),
                new PermissionSeed("GRN_VIEW", "View GRN", "GRN"),
                new PermissionSeed("OPENING_STOCK_CREATE", "Create/update opening stock", "Opening Stock"),
                new PermissionSeed("OPENING_STOCK_APPROVE", "Approve/cancel opening stock", "Opening Stock"),
                new PermissionSeed("OPENING_STOCK_VIEW", "View opening stock", "Opening Stock"),
                new PermissionSeed("STOCK_ADJUSTMENT_CREATE", "Create stock adjustment", "Stock Adjustment"),
                new PermissionSeed("STOCK_ADJUSTMENT_VIEW", "View stock adjustment", "Stock Adjustment"),
                new PermissionSeed("STOCK_ISSUE_CREATE", "Create/update stock issue", "Stock Issue"),
                new PermissionSeed("STOCK_ISSUE_APPROVE", "Approve stock issue", "Stock Issue"),
                new PermissionSeed("STOCK_TRANSFER_CREATE", "Create/view stock transfer", "Stock Transfer"),
                new PermissionSeed("STOCK_TRANSFER_UPDATE", "Update stock transfer", "Stock Transfer"),
                new PermissionSeed("STOCK_TRANSFER_APPROVE", "Approve stock transfer", "Stock Transfer"),
                new PermissionSeed("STOCK_TRANSFER_CANCEL", "Cancel stock transfer", "Stock Transfer"),
                new PermissionSeed("ITEM_GROUP_MANAGE", "Create/update item groups", "Item Master"),
                new PermissionSeed("ITEM_MANAGE", "Create/update items", "Item Master"),
                new PermissionSeed("SYSTEM_MANAGEMENT_MANAGE", "Manage locations, suppliers, roles and permissions", "System Management")
        );

        Map<String, Permission> byCode = new HashMap<>();
        for (PermissionSeed seed : catalog) {
            Permission permission = permissionRepository.findByCode(seed.code()).orElseGet(() -> {
                Permission p = new Permission();
                p.setCode(seed.code());
                p.setDescription(seed.description());
                p.setCategory(seed.category());
                return permissionRepository.save(p);
            });
            byCode.put(seed.code(), permission);
        }
        return byCode;
    }

    private Map<String, Role> seedRoles(Map<String, Permission> permissions) {
        Map<String, Set<String>> roleToPermissionCodes = new HashMap<>();
        roleToPermissionCodes.put("SYSTEM_ADMIN", permissions.keySet());
        roleToPermissionCodes.put("STORE_STAFF", Set.of(
                "GRN_CREATE", "GRN_VIEW", "OPENING_STOCK_CREATE", "OPENING_STOCK_VIEW",
                "STOCK_ADJUSTMENT_CREATE", "STOCK_ADJUSTMENT_VIEW", "STOCK_ISSUE_CREATE",
                "STOCK_TRANSFER_CREATE", "STOCK_TRANSFER_UPDATE", "STOCK_TRANSFER_CANCEL",
                "ITEM_GROUP_MANAGE", "ITEM_MANAGE"
        ));
        roleToPermissionCodes.put("APPROVING_AUTHORITY", Set.of(
                "GRN_APPROVE", "GRN_VIEW", "OPENING_STOCK_APPROVE", "OPENING_STOCK_VIEW",
                "STOCK_ADJUSTMENT_VIEW", "STOCK_ISSUE_APPROVE", "STOCK_TRANSFER_CREATE",
                "STOCK_TRANSFER_UPDATE", "STOCK_TRANSFER_APPROVE", "STOCK_TRANSFER_CANCEL"
        ));
        roleToPermissionCodes.put("REQUESTING_OFFICER", Set.of(
                "STOCK_TRANSFER_CREATE", "GRN_VIEW", "OPENING_STOCK_VIEW", "STOCK_ADJUSTMENT_VIEW"
        ));
        roleToPermissionCodes.put("FINANCIAL_SUPPLIER", Set.of(
                "STOCK_TRANSFER_CREATE", "GRN_VIEW", "OPENING_STOCK_VIEW", "STOCK_ADJUSTMENT_VIEW"
        ));

        Map<String, String> displayNames = Map.of(
                "SYSTEM_ADMIN", "System Admin",
                "STORE_STAFF", "Store Staff",
                "APPROVING_AUTHORITY", "Approving Authority",
                "REQUESTING_OFFICER", "Requesting Officer",
                "FINANCIAL_SUPPLIER", "Financial Supplier"
        );

        Map<String, Role> byCode = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : roleToPermissionCodes.entrySet()) {
            String code = entry.getKey();
            Role role = roleRepository.findByCode(code).orElseGet(Role::new);
            role.setCode(code);
            role.setName(displayNames.get(code));
            role.setSystemDefined(true);
            Set<Permission> grantedPermissions = new HashSet<>();
            for (String permissionCode : entry.getValue()) {
                grantedPermissions.add(permissions.get(permissionCode));
            }
            role.setPermissions(grantedPermissions);
            byCode.put(code, roleRepository.save(role));
        }
        return byCode;
    }

    private void seedLocations() {
        Map<String, String> locations = new HashMap<>();
        locations.put("HEAD_OFFICE", "Head Office");
        locations.put("KALUTARA", "Kalutara");
        locations.put("KANDY", "Kandy");
        locations.put("GALLE", "Galle");
        locations.put("GAMPAHA", "Gampaha");
        locations.put("ANURADHAPURA", "Anuradhapura");

        for (Map.Entry<String, String> entry : locations.entrySet()) {
            if (locationRepository.findByCode(entry.getKey()).isEmpty()) {
                Location location = new Location();
                location.setCode(entry.getKey());
                location.setName(entry.getValue());
                location.setActive(true);
                locationRepository.save(location);
            }
        }
    }

    private void seedSuppliers() {
        Map<String, String> suppliers = new HashMap<>();
        suppliers.put("SURASAVI_SUPPLIERS", "Surasavi Suppliers");
        suppliers.put("GUNASEKARA_SUPPLIERS", "Gunasekara Suppliers");
        suppliers.put("ATLAS_SUPPLIERS", "Atlas Suppliers");
        suppliers.put("ALPHA_STATIONERIES", "Alpha Stationeries");
        suppliers.put("INNOVATION_TECH", "Innovation Tech");

        for (Map.Entry<String, String> entry : suppliers.entrySet()) {
            if (supplierRepository.findByCode(entry.getKey()).isEmpty()) {
                Supplier supplier = new Supplier();
                supplier.setCode(entry.getKey());
                supplier.setName(entry.getValue());
                supplier.setActive(true);
                supplierRepository.save(supplier);
            }
        }
    }

    private void seedBootstrapAdmin(Map<String, Role> roles) {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setRole(roles.get("SYSTEM_ADMIN"));
        admin.setLocation(locationRepository.findByCode("HEAD_OFFICE").orElseThrow());
        userRepository.save(admin);
    }
}
