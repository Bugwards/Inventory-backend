package com.example.inventoryAuth.DTO.StockReceiptDto;

import com.example.inventoryAuth.Entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockReceiptRequest {
    private LocalDate date;
    private Long fromLocationId;
    private Long receiptLocationId;
    private String transferNo;
    private String comment;
    private List<StockReceiptItemsRequest> stockReceiptItems;

    private Status status;

    private String approvedBy;
    private LocalDate approvedDate;
    private LocalDateTime approvedAt;

    private String cancelledBy;
    private LocalDateTime cancelledAt;
    private String cancelReason;

}
