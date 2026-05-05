package com.example.inventoryAuth.DTO.StockTransferDto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferredItemResponse {

    private String itemGroup;

    private String itemCode;

    private String itemName;

    private String description;

    private String unitOfMeasurement;

    private Integer transferQty;

    private List<TransferredGrnResponse> tranferredgrnitem;

}
