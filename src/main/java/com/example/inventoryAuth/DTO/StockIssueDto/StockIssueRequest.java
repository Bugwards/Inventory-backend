package com.example.inventoryAuth.DTO.StockIssueDto;



import com.example.inventoryAuth.Entity.Department;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockIssueRequest {
    private LocalDate date;

    private Department department;

    private String requestRef;

    private String comment;

    private List<ItemIssuedDto> items;
}
