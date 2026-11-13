package com.example.inventoryAuth.DTO;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OpeningStockDTO {

    private LocalDate openingDate;
    private String comment;

    private List<OpeningStockItemDTO> items;
}
