package com.example.inventoryAuth.Repository;


import com.example.inventoryAuth.Entity.StockTransfer;
import com.example.inventoryAuth.Entity.StockTransferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransferItemRepository extends JpaRepository<StockTransferItem,Long> {
    List<StockTransferItem> findByStockTransfer(StockTransfer stockTransfer);
}
