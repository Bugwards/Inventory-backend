package com.example.inventoryAuth.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class RecentTransactionDTO {

    private LocalDate date;
    private String type;
    private String reference;
    private String itemCode;
    private String itemName;
    private Long quantity;
    private Long balance;
    private String remarks;

    public RecentTransactionDTO() {
    }

    public RecentTransactionDTO(LocalDate date, String s, String reference, String itemCode, String itemName, long quantity, long l, String remarks) {
    }
}