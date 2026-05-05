package com.example.inventoryAuth.DTO.StockIssueDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrnWiseStockDto {

    private String grnNumber;
    private LocalDate grnDate;
    private String unitOfMeasurement;
    private Long currentQty;
    private Double unitPrice;
}
