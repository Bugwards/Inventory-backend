package com.example.inventoryAuth.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GRNDTO {

    private String poNumber;
    private LocalDate grnDate;
    private Long supplierId;
    private String comment;

    private List<GRNItemDTO> items;


}
