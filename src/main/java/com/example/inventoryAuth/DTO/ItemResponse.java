package com.example.inventoryAuth.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ItemResponse {
    private String itemCode;
    private String itemName;
    private String itemGroupName;
    private String itemDescription;
    private String unitOfMeasurement;
    private Boolean active;
}
