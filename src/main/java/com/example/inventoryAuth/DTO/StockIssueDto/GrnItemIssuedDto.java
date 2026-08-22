package com.example.inventoryAuth.DTO.StockIssueDto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GrnItemIssuedDto {

    private String grnNumber;

    private LocalDate grnDate;

    private Long currentQuantity;

    private Integer issuedQuantity;
}
