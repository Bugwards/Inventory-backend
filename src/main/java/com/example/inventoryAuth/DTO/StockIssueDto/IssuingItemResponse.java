package com.example.inventoryAuth.DTO.StockIssueDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssuingItemResponse{

    private String itemGroupName;

    private String itemCode;

    private String itemName;

    private String itemDescription;

    private List<GrnWiseStockDto> grnStockList;

}