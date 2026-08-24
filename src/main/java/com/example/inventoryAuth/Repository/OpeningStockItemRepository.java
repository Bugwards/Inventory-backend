package com.example.inventoryAuth.Repository;

import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Entity.OpeningStockItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OpeningStockItemRepository extends JpaRepository<OpeningStockItem, Long> {
    List<OpeningStockItem> findByItemAndOpeningStock_OpeningDateAfter(Item item, LocalDate localDate);
}
