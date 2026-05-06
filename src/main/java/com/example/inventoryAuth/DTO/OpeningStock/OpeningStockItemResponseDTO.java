package com.example.inventoryAuth.DTO.OpeningStock;


import lombok.Data;

@Data
public class OpeningStockItemResponseDTO {

    private Long openingStockItemId;
    private String itemCode;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
}