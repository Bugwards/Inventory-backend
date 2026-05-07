package com.example.inventoryAuth.DTO.ReortDto;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GrnWiseReportResponse {

    private String itemGroupName;

    private String itemCode;

    private String itemName;

    private String description;

    private String unit;

    private Long currentBalance;

    private Long actualBalance;

    private double value;


    private List<String> grnNumber;

    private List<LocalDate> grnDate;

}
