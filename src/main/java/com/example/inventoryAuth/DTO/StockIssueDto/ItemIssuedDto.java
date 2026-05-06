package com.example.inventoryAuth.DTO.StockIssueDto;

import lombok.Data;

import java.util.List;

@Data
public class ItemIssuedDto {

    private String itemCode;

    private String itemGroupName;

    private String itemName;

    private String description;

    private String unitOfMesuremnet;

    private Long totalIssuedQuantity;

    private List<GrnItemIssuedDto> grnItems;

}
