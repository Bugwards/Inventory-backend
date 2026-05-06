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

    private Location fromLocation;
    private Location receiptLocation;

    private String Comment;

    private String transferNo;

    @Enumerated (EnumType.STRING)
    private Status status = Status.UNAPPROVED;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="stock_transfer_id")
    private StockTransfer stockTransfer;

    @OneToMany(mappedBy="stockReceipt" ,cascade = CascadeType.ALL)
    private List<StockReceiptItem> stockReceiptItem = new ArrayList<>();;

    private LocalDate approvedDate;

    private String cancelReason;

}
