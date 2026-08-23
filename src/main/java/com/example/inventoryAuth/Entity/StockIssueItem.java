package com.example.inventoryAuth.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockIssueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Many items belong to one StockIssue
    @ManyToOne
    @JoinColumn(name = "stock_issue_id", nullable = false)
    private StockIssue stockIssue;

    // ✅ Many records can reference same Item
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    // ✅ Important business fields
    @Column(nullable = false)
    private Integer issuedQuantity;

    @ManyToOne
    @JoinColumn(name = "grn_item_id")
    private GRNItem grnItem;

//    private Long issuedPrice; // optional
}