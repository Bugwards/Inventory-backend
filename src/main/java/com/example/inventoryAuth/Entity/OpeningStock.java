package com.example.inventoryAuth.Entity;






import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.ReferenceType;
import com.example.inventoryAuth.Entity.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "opening_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpeningStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openingStockId;

    @Column(unique = true)
    private String entryNo;

    @Column(nullable = false)
    private LocalDate openingDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;

    @Enumerated(EnumType.STRING)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @OneToMany(mappedBy = "openingStock",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OpeningStockItem> items;

    private Double totalValue;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}