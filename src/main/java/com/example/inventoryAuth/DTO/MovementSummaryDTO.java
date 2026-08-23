package com.example.inventoryAuth.DTO;

import lombok.*;

@Getter@Setter
public class MovementSummaryDTO {

    private String type;
    private long quantity;
    private long transactionCount;

    public MovementSummaryDTO() {
    }

    public MovementSummaryDTO(
            String type,
            long quantity,
            long transactions
    ) {
        this.type = type;
        this.quantity = quantity;
        this.transactionCount = transactions;
    }

}