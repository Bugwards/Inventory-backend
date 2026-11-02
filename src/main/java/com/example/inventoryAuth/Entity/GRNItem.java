package com.example.inventoryAuth.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

@Entity
@Table(name = "grn_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GRNItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grnItemId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer currentQty;

    @Column(nullable = false)
    private Integer actualQty;

    @Column(nullable = false)
    private Double unitPrice;

    @ManyToOne
    @JoinColumn(name = "grn_id")
    @JsonIgnore
    private GRN grn;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private Integer grnWiseTransferredQuantity;

    private Integer grnWiseReceivedQuantity;
}