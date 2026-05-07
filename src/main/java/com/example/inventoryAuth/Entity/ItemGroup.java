package com.example.inventoryAuth.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "item_groups")
public class ItemGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "item_group_code",
            unique = true,
            nullable = false
    )
    private String code;

    @Column(
            name = "item_group_name",
            unique = true,
            nullable = false
    )
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "maintain_reorder_level")
    private Boolean maintainReorder;

    @Column(name = "gl_account")
    private String glAccount;

    @OneToMany(mappedBy = "itemGroup")
    private List<Item> items;
}