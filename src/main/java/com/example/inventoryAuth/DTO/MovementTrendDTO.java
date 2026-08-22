package com.example.inventoryAuth.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MovementTrendDTO {

    private String month;
    private long inward;
    private long outward;

    public MovementTrendDTO() {
    }

    public MovementTrendDTO(
            String month,
            long inward,
            long outward
    ) {
        this.month = month;
        this.inward = inward;
        this.outward = outward;
    }

}