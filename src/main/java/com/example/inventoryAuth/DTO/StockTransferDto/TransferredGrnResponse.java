package com.example.inventoryAuth.DTO.StockTransferDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

//grn details of a transfer
public class TransferredGrnResponse {
    private String grnNumber;

    private LocalDate grnDate;

    private Integer currentQuantity;

    private Integer TransferQty;

}
