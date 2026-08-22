package com.example.inventoryAuth.DTO.StockIssueDto;


import com.example.inventoryAuth.Entity.Department;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Status;
import lombok.Data;

import java.lang.reflect.AccessFlag;
import java.time.LocalDate;

@Data
public class StockIssueResponse {
    private String issueNo;
    private LocalDate issueDate;
    private Location location;
    private Status status;
    private Department department;
    private LocalDate approvedDate;
}
