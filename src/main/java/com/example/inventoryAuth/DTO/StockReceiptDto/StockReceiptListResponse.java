package com.example.inventoryAuth.DTO.StockReceiptDto;

import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReceiptListResponse {
    private String receiptNo;
    private LocalDate receiptDate;
    private String transferNo;
    private Location fromLocation;
    private Location receiptLocation;
    private Status status;
    private LocalDate approvedDate;

    private String approvedBy;
    private LocalDateTime approvedAt;

    private String cancelledBy;
    private LocalDateTime cancelledAt;

    private String cancelReason;
}
