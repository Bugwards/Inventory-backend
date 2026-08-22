package com.example.inventoryAuth.DTO;


import com.example.inventoryAuth.Entity.Location;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StockAdjustmentDTO {

    private LocalDate adjustmentDate;
    private String reason;
    private String comment;
    private Location location;

    private List<StockAdjustmentItemDTO> items;
}