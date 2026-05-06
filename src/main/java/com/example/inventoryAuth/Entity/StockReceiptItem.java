package com.example.inventoryAuth.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StockReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer receivedQty;

    @ManyToOne
    @JoinColumn(name="item_id",nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name="grn_item_id",nullable = false)
    private GRNItem grnItem;

    @ManyToOne
    @JoinColumn(name="stock_recipt_id",nullable = false)
    private StockReceipt stockReceipt;

    private String quantityMismatchReason;

}
