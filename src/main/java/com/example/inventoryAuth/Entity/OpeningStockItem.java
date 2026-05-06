package com.example.inventoryAuth.Entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "opening_stock_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpeningStockItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openingStockItemId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "opening_stock_id")
    @JsonIgnore
    private OpeningStock openingStock;
}
