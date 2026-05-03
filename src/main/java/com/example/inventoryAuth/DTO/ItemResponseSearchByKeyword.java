package com.example.inventoryAuth.DTO;

import lombok.Data;

@Data
public class ItemResponseSearchByKeyword {
    private String itemCode;
    private String itemName;
    private String description;
}
