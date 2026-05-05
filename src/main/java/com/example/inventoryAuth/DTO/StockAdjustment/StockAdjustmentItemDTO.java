package com.example.inventoryAuth.DTO.StockAdjustment;

import com.example.inventoryAuth.Entity.GRN;
import com.example.inventoryAuth.Entity.Stock;
import lombok.Data;

@Data
public class StockAdjustmentItemDTO {

    private Long stockId;
    private Long grnId;
    private Integer quantity;
}