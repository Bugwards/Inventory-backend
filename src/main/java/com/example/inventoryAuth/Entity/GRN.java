package com.example.inventoryAuth.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "grn")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GRN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grnId;

    @Column(unique = true)
    private String grnNumber;

    @Column(nullable = false)
    private String poNumber;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private LocalDate grnDate;

    private Double totalValue;

    private String comment;


    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime cancelledAt;
    private String cancelReason;

    @Enumerated(EnumType.STRING)
    private Supplier supplier;

    @Enumerated(EnumType.STRING)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GRNItem> items;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }


}