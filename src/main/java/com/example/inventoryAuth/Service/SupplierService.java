package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.Entity.Supplier;
import com.example.inventoryAuth.Repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    public Supplier create(Supplier supplier) {
        supplier.setId(null);
        if (supplier.getActive() == null) {
            supplier.setActive(true);
        }
        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    public List<Supplier> getActive() {
        return supplierRepository.findByActiveTrue();
    }

    public Supplier update(Long id, Supplier update) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        existing.setCode(update.getCode());
        existing.setName(update.getName());
        return supplierRepository.save(existing);
    }

    public Supplier setActive(Long id, boolean active) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        existing.setActive(active);
        return supplierRepository.save(existing);
    }
}
