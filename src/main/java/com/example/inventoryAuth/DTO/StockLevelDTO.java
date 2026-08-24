package com.example.inventoryAuth.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockLevelDTO {

    private String itemCode;
    private String itemName;
    private long currentStock;
    private long minimumLevel;
    private long reorderLevel;
    private String status;

    public StockLevelDTO() {
    }

    public StockLevelDTO(
            String itemCode,
            String itemName,
            long currentStock,
            long minimumLevel,
            long reorderLevel,
            String status
    ) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.currentStock = currentStock;
        this.minimumLevel = minimumLevel;
        this.reorderLevel = reorderLevel;
        this.status = status;
    }

}