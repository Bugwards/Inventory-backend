package com.example.inventoryAuth.Repository;

import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Entity.StockAdjustmentItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockAdjustmentItemRepository extends JpaRepository<StockAdjustmentItem, Long> {

    List<StockAdjustmentItem> findByStock_ItemAndAdjustment_AdjustmentDateAfter(Item item, LocalDate localDate);
}
