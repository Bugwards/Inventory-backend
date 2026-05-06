package com.example.inventoryAuth.Entity;





import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stock_adjustment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adjustmentId;


    @Column(unique = true,nullable = false)
    private String adjustmentNo;


    @Column(nullable = false)
    private LocalDate adjustmentDate;

    @Enumerated(EnumType.STRING)
    private Status status;


    @Column(nullable = false)
    private String reason;
    private String comment;


    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @OneToMany(mappedBy = "adjustment",
            cascade = CascadeType.ALL,
            orphanRemoval = true)

    private List<StockAdjustmentItem> items;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}