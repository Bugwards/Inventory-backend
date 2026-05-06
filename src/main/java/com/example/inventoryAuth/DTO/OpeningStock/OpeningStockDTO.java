package com.example.inventoryAuth.DTO.OpeningStock;

import com.example.inventoryAuth.Entity.Location;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OpeningStockDTO {

    private LocalDate openingDate;
    private Location location;
    private String comment;

    private List<OpeningStockItemDTO> items;
}
