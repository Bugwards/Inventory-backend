package com.example.inventoryAuth.Entity;



import com.example.inventoryAuth.Entity.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "bin_card")
public class BinCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private Item item;

    @Column(nullable = false)
    private LocalDate date;//transfer/supply date, should take from StockIssued OR StockTransfer OR Grn Tables

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // INWARD or OUTWARD

    @Column(length = 50)
    private String reference;//grnNumber from Grn Table

    private Long inwardQty;//current quantity+ quantity(GrnItem)

    private Long outwardQty;//stockIssuedQuantity(from grnItemIssuedDto) or StockTransferQuantity(from GrnItemTransferredDto) OR stockIssuedQuantity + StockTransferQuantity


    @Column(nullable = false)
    private Long balance;//if supply happens-> current quantity+ quantity(from GrnItem)/ if transfer or issue happens ->  current quantity-stockIssuedQuantity or StockTransferQuantity

    @Column(length = 255)
    private String remarks;
}

