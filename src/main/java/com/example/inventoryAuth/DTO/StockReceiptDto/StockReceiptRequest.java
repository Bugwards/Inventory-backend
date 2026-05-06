package com.example.inventoryAuth.DTO.StockReceiptDto;

import com.example.inventoryAuth.Entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReceiptRequest {

    private LocalDate date;
    private Location fromLocation;
    private Location reciptLocation;
    private String transferNo;
    private String comment;

   private List<StockReceiptItemsRequest>  stockReceiptItems;
}
