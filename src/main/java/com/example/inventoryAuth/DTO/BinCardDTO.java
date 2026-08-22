package com.example.inventoryAuth.DTO;


import com.example.inventoryAuth.Entity.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter@Setter
public class BinCardDTO {
    private LocalDate date;
    private TransactionType type;
    private String reference;

    private Long inwardQty;
    private Long outwardQty;
    private Long balance;

    private String remarks;

    private String itemCode;
    private String itemName;
}
