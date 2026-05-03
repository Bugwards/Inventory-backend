package com.example.inventoryAuth.DTO.StockTransferDto;


import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTransferListResponse {
    private String transferNo;
    private LocalDate transferDate;
    private Location fromLocation;
    private Location toLocation;
    private Status status;
    private LocalDate approvedDate;





}
