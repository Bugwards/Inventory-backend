package com.example.inventoryAuth.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ManyToAny;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="stock_transfer_items")
public class StockTransferItem {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="stock_transfer_id",nullable = false)
    private StockTransfer stockTransfer;

    @ManyToOne
    @JoinColumn(name="item_id",nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name="grn_item_id",nullable = false)
    private GRNItem grnItem;

    private Integer transferQty;



}
