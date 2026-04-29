package com.example.inventoryAuth.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemDTO {

    private String itemName;
    private String itemCode;
    private String itemDescription;
    private Boolean active;
    private Boolean maintainReorder;
    private Integer reorderQuantity;
    private Integer minimumLevel;
}
