package com.example.inventoryAuth.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Location fromLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Location toLocation;

    private String requestRef;

    private String comment;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNAPPROVED;

    private LocalDate approvedDate;

    private String canselReason;

    @OneToMany(mappedBy = "stockTransfer" ,cascade = CascadeType.ALL) //StockTransferItem eke  id (PK)ekth ekk map krgnnw
    private List<StockTransferItem> stockTransferItem = new ArrayList<>(); //ek stock transfer ekkt item godk tyen nis list ekk widiht ganne

//    @OneToOne(mappedBy="stockTransfer")
//    private StockReceipt stockTransferReceipt;

}

