package com.example.inventoryAuth.DTO.StockTransferDto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

//give StockTransferDetails
public class StockTransferRequest {
    private LocalDate date;

    private Long toLocationId;

    private String requestRef;

    private String comment;

    private List<TransferredItemResponse> items;

    private String approvedBy;

    private LocalDateTime approvedAt;

    private String cancelledBy;

    private LocalDateTime cancelledAt;

}
