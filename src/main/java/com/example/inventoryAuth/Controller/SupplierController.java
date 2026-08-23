package com.example.inventoryAuth.Controller;

import com.example.inventoryAuth.Entity.Supplier;
import com.example.inventoryAuth.Service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    SupplierService supplierService;

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @GetMapping
    public List<Supplier> getAll() {
        return supplierService.getAll();
    }

    @GetMapping("/active")
    public List<Supplier> getActive() {
        return supplierService.getActive();
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PostMapping
    public Supplier create(@RequestBody Supplier supplier) {
        return supplierService.create(supplier);
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @RequestBody Supplier supplier) {
        return supplierService.update(id, supplier);
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PatchMapping("/{id}/deactivate")
    public Supplier deactivate(@PathVariable Long id) {
        return supplierService.setActive(id, false);
    }

    @PreAuthorize("hasAuthority('SYSTEM_MANAGEMENT_MANAGE')")
    @PatchMapping("/{id}/activate")
    public Supplier activate(@PathVariable Long id) {
        return supplierService.setActive(id, true);
    }
}
