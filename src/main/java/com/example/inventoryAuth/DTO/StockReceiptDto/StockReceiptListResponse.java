package com.example.inventoryAuth.DTO.StockReceiptDto;

import com.example.inventoryAuth.Entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReceiptListResponse {
    private String receiptNo;
    private LocalDate receiptDate;
    private String transferNo;
    private Location fromLocation;
    private Location receiptLocation;
    private Status status;
    private LocalDate approvedDate;

}
