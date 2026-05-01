package com.example.inventoryAuth.Entity;

<<<<<<< HEAD


import com.fasterxml.jackson.annotation.JsonIgnore;
=======
>>>>>>> aae77b0cf1d385ca1513e1d4cf8901adc6e1ea1b
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String itemCode;

    private String itemName;

    private String itemGroupName;//remove this

    @Column(nullable = true)
    private String itemDescription;

    private String unitOfMeasurement;

    private Boolean active;

    private Boolean maintainReorder;

    private Integer reorderQuantity;

    private Integer minimumLevel;

    @ManyToOne
    @JoinColumn(name = "item_group_id" , nullable = false)
<<<<<<< HEAD
    @JsonIgnore
=======
>>>>>>> aae77b0cf1d385ca1513e1d4cf8901adc6e1ea1b
    private ItemGroup itemGroup;

}
