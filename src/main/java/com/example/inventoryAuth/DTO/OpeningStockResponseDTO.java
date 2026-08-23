package com.example.inventoryAuth.DTO;



import com.example.inventoryAuth.Entity.Location;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class OpeningStockResponseDTO {


    private Long openingStockId;

    private String entryNo;
    private Location location;
    private LocalDate openingDate;
    private Double totalValue;
    private String status;

    private List<OpeningStockItemResponseDTO> items;
}
