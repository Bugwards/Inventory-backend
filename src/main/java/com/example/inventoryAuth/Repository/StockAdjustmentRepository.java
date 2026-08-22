package com.example.inventoryAuth.Repository;

import com.example.inventoryAuth.Entity.StockAdjustment;
import com.example.inventoryAuth.Entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {

    List<StockAdjustment> findByStatus(String status);

    List<StockAdjustment> findByLocation(Location location);

    List<StockAdjustment> findByAdjustmentDateBetween(Date from, Date to);

    List<StockAdjustment> findByCreatedById(Long userId);

    List<StockAdjustment> findByApprovedById(Long userId);
}