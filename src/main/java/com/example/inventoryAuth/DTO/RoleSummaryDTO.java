package com.example.inventoryAuth.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleSummaryDTO {
    private Long id;
    private String code;
    private String name;
}
