package com.example.inventoryAuth.DTO;



import com.example.inventoryAuth.Entity.Location;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockViewDTO {

    private Long stockId;

    private String itemCode;
    private String itemName;

    private Long grnId;
    private String grnNo;

    private Location location;

    private Integer currentQty;
    private Integer actualQty;

    private Double unitPrice;

    private Integer adjustedQty;

    private LocalDateTime createdAt;
}