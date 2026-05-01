package com.example.inventoryAuth.DTO;



import lombok.Data;

@Data
public class GRNItemResponseDTO {

    private Long grnItemId;
    private String itemCode;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
}
