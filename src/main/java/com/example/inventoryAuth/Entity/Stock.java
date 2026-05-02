package com.example.inventoryAuth.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    private Integer currentQty;
    private Integer actualQty;

    private Double unitPrice;


    private Long referenceId;

    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Enumerated(EnumType.STRING)
    private Location location;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "grn_item_id", nullable = false)
    private GRNItem grnItem;


}