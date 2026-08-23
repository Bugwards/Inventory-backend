package com.example.inventoryAuth.DTO.StockTransferDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
//grnDetails after select item
public class SelectedItemGrnResponse {

    private String grnNumber;
    private LocalDate grnDate;
    private String unit;
    private Long currentQuantity;
    private Double pricePerUnit;

}
