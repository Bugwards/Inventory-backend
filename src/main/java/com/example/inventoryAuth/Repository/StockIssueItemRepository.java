package com.example.inventoryAuth.Repository;

import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Entity.StockIssueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockIssueItemRepository extends JpaRepository<StockIssueItem, Long> {
    List<StockIssueItem> findByItemAndStockIssue_DateAfter(Item item, LocalDate localDate);
}
