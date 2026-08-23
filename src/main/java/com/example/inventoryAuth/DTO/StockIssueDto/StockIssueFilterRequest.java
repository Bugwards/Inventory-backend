package com.example.inventoryAuth.DTO.StockIssueDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StockIssueFilterRequest {

    private String issueDateFilter;
    // ALL, THIS_MONTH, LAST_AND_THIS_MONTH, DATE_RANGE

    private LocalDate fromDate;
    private LocalDate toDate;

    private String status;

    private String location;
    private String department;

    private String search;

    private String sortBy;
    private String sortDirection;
}