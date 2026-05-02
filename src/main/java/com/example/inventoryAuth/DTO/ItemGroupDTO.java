package com.example.inventoryAuth.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemGroupDTO {
    private String name;
    private String description;
    private Boolean maintainReorder;
    private String glAccount;
}

