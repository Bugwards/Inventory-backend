package com.example.inventoryAuth.Repository;



import com.example.inventoryAuth.Entity.OpeningStock;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OpeningStockRepository extends JpaRepository<OpeningStock, Long> {

    List<OpeningStock> findByStatus(Status status);

    List<OpeningStock> findByLocation(Location location);

    List<OpeningStock> findByCreatedById(Long id);

    List<OpeningStock> findByApprovedById(Long id);

    List<OpeningStock> findByOpeningDateBetween(LocalDate from, LocalDate to);
}