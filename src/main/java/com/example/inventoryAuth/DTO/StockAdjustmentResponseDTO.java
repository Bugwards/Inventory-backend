package com.example.inventoryAuth.DTO;


import com.example.inventoryAuth.Entity.Location;
import  com.example.inventoryAuth.Entity.Status;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StockAdjustmentResponseDTO {

    private Long adjustmentId;
    private String adjustmentNo;
    private LocalDate adjustmentDate;
    private String status;

    private String reason;
    private String comment;



    private Location location;

    private String createdBy;
    private String approvedBy;

    private List<StockAdjustmentItemResponseDTO> items;
}