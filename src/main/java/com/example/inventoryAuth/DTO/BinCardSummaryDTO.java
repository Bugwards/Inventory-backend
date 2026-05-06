package com.example.inventoryAuth.DTO;


import com.example.inventoryAuth.Entity.MovementType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.List;

@Data
public class BinCardSummaryDTO {

    // 🔹 Item Info
    private String itemCode;
    private String itemName;

    // 🔹 Item Levels
    private Integer minimumLevel;
    private Integer reorderLevel;


    // 🔹 Movement Totals
    private Long totalInward;
    private Long totalOutward;

    // 🔹 Analytics
    private Double averageMonthlyUsage;
    @Enumerated(EnumType.STRING)
    private MovementType movementType; // FAST / SLOW

    // 🔹 Full Bin Card History
    private List<BinCardDTO> history;
}
