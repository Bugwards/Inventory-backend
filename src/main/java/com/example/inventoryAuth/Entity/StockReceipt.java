package com.example.inventoryAuth.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StockReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String receiptNo;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_location_id")
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receipt_location_id")
    private Location ReceiptLocation;

    private String comment;

    private String transferNo;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNAPPROVED;

    @OneToOne
    @JoinColumn(name = "stock_transfer_id")
    private StockTransfer stockTransfer;

    @OneToMany(mappedBy = "stockReceipt", cascade = CascadeType.ALL)
    private List<StockReceiptItem> stockReceiptItem = new ArrayList<>();

    private LocalDate approvedDate;
    private String approvedBy;
    private java.time.LocalDateTime approvedAt;

    private String cancelledBy;
    private java.time.LocalDateTime cancelledAt;
    private String cancelReason;
}
