package com.example.inventoryAuth.DTO;

import  com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Supplier;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GRNDTO {

    private String poNumber;
    private LocalDate grnDate;
    private Supplier supplier;
    private String comment;

    private List<GRNItemDTO> items;


}