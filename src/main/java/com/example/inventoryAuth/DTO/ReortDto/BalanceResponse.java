package com.example.inventoryAuth.DTO.ReortDto;

import lombok.Data;

@Data
public class BalanceResponse {

        private String itemName;

        private String itemGroupName;

        private String itemCode;

        private String description;

        private String unit;

        private Long currentBalance;

        private Long actualBalance;

        private Long value;


    }

