package com.example.inventoryAuth.Controller;



import com.example.inventoryAuth.DTO.StockAdjustmentDTO;
import com.example.inventoryAuth.DTO.StockAdjustmentResponseDTO;
import com.example.inventoryAuth.Entity.StockAdjustment;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Service.StockAdjustmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-adjustment")
@CrossOrigin(origins = "http://localhost:3000")
public class StockAdjustmentController {

    private final StockAdjustmentService service;

    public StockAdjustmentController(StockAdjustmentService service) {
        this.service = service;
    }

    //  CREATE + APPROVE
    @PreAuthorize("hasAnyRole('STORE_STAFF','SYSTEM_ADMIN')")
    @PostMapping
    public StockAdjustment create(@RequestBody StockAdjustmentDTO dto) {
        return service.save(dto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public StockAdjustmentResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<StockAdjustment> search(
            @RequestParam(required = false) Location location,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return service.search(location, keyword, dateFilter, fromDate, toDate);
    }
}
