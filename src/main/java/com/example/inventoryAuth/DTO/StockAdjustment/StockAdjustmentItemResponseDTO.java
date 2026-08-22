package com.example.inventoryAuth.DTO.StockAdjustment;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockAdjustmentItemResponseDTO {

    private Long adjustmentItemId;

    private Long stockId;
    private String itemCode;
    private String itemName;

    private Long grnId;
    private String grnNo;

    private Integer quantity;
}