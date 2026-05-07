package com.example.inventoryAuth.DTO.ReortDto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class ReOrderResponse {

    private String itemName;

    private String itemCode;

    private String description;

    private String unit;


    private Long currentBalance;

    private Long actualBalance;


    private Integer minLevelQuantity;

    private Integer reorderQuantity ;

}

