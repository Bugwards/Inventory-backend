package com.example.inventoryAuth.DTO;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class StockAdjustmentDTO {

    private LocalDate adjustmentDate;
    private String reason;
    private String comment;

    private List<StockAdjustmentItemDTO> items;
}
