package com.example.inventoryAuth.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "item_group")
public class ItemGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    private Boolean maintainReorder;

    private String glAccount;

    @OneToMany(mappedBy = "itemGroup")
    private List<Item> items;

}

