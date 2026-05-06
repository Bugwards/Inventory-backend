package com.example.inventoryAuth.DTO.StockReceiptDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReceiptItemsRequest {

    private String itemGroup;
    private String itemCode;
    private String itemName;
    private String description;
    private String unitOfMeasurement;
    private Long transferredQty;
    private Integer receivedQty;

    private List<StockReceiptItemGrnRequest>   stockReceiptItemGrn;


}
