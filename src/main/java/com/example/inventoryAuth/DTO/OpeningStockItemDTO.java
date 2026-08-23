package com.example.inventoryAuth.DTO;



import lombok.Data;

@Data
public class OpeningStockItemDTO {


    private String itemCode;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
}