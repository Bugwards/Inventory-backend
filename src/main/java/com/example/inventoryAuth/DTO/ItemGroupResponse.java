package com.example.inventoryAuth.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ItemGroupResponse {
    private String code;
    private String name;
    private String description;
    private Boolean maintainReorder;
    private String glAccount;
}
