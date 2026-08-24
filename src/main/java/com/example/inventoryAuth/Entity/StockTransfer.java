package com.example.inventoryAuth.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_transfers")
public class StockTransfer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String transferNo;

    @Column(nullable = false)
    private LocalDate transferDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "from_location_id", nullable = false)
    private Location fromLocation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "to_location_id", nullable = false)
    private Location toLocation;

    private String requestRef;

    private String comment;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNAPPROVED;

    private LocalDate approvedDate;

    private String approvedBy;

    private java.time.LocalDateTime approvedAt;

    private String cancelledBy;

    private java.time.LocalDateTime cancelledAt;

    private String canselReason;

    @OneToMany(mappedBy = "stockTransfer" ,cascade = CascadeType.ALL) //StockTransferItem eke  id (PK)ekth ekk map krgnnw
    private List<StockTransferItem> stockTransferItem = new ArrayList<>(); //ek stock transfer ekkt item godk tyen nis list ekk widiht ganne

//    @OneToOne(mappedBy="stockTransfer")
//    private StockReceipt stockTransferReceipt;

}

