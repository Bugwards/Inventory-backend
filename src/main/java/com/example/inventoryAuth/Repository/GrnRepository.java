package com.example.inventoryAuth.Repository;

import com.example.inventoryAuth.Entity.GRN;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Status;
import com.example.inventoryAuth.Entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface GrnRepository extends JpaRepository<GRN, Long> {

    List<GRN> findByStatus(Status status);

    List<GRN> findBySupplier(Supplier supplier);

    List<GRN> findByLocation(Location location);

    List<GRN> findByGrnDateBetween(LocalDate from, LocalDate to);

    List<GRN> findByCreatedById(Long id);

    List<GRN> findByApprovedById(Long id);

    GRN findByGrnNumber(String GrnNumber);
}