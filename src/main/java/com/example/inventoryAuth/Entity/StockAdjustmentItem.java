package com.example.inventoryAuth.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_adjustment_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adjustment_item_id")
    private Long adjustmentItemId;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "adjustment_id")
    @JsonIgnore
    private StockAdjustment adjustment;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @ManyToOne
    @JoinColumn(name = "grn_id")
    private GRN grn;
}