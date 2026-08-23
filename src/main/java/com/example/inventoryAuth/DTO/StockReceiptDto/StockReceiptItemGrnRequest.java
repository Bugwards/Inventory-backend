package com.example.inventoryAuth.DTO.StockReceiptDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReceiptItemGrnRequest {
    private String grnNo;
    private LocalDate grnDate;
    private Long transferredQty;
    private Long receivedQty;
}
