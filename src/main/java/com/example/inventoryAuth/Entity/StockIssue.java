package com.example.inventoryAuth.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class StockIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    private String issueNo;

    @Enumerated(EnumType.STRING)
    private Location currentLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Department department;

    private String requestRef;

    private String comment;

    private String cancelMsg;

    @Enumerated(EnumType.STRING)
    private Status status = Status.UNAPPROVED;

    // ✅ One StockIssue has many StockIssueItems
    @OneToMany(mappedBy = "stockIssue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockIssueItem> issueItems = new ArrayList<>();


    private LocalDate approvedDate;

}