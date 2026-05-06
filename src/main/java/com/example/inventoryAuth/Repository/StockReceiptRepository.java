package com.example.inventoryAuth.Repository;


import com.example.inventoryAuth.Entity.StockReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockReceiptRepository extends JpaRepository<StockReceipt, Long> {
    StockReceipt findByReceiptNo(String receiptNo);

}
