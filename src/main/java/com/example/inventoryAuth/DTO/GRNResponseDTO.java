package com.example.inventoryAuth.DTO;


import com.example.inventoryAuth.Entity.Location;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class GRNResponseDTO {

    private Long grnId;
    private Location location;
    private String grnNumber;
    private String poNumber;
    private String supplier;
    private LocalDate grnDate;
    private Double totalValue;
    private String status;
    private String createdBy;
    private String approvedBy;
    private String comment;

    private List<GRNItemResponseDTO> items;
}