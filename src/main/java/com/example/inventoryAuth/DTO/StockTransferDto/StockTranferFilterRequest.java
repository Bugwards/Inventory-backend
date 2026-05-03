package com.example.inventoryAuth.DTO.StockTransferDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockTranferFilterRequest {

    private String transferDate;
    private String status;
    private String fromLocation;
    private String toLocation;
    private String transferNo;
    private String sortBy;
    private String sortOrder;

    private LocalDate fromDate;
    private LocalDate toDate;
}
