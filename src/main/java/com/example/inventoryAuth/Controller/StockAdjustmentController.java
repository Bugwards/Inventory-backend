package com.example.inventoryAuth.Controller;



import com.example.inventoryAuth.DTO.StockAdjustmentDTO;
import com.example.inventoryAuth.DTO.StockAdjustmentResponseDTO;
import com.example.inventoryAuth.Entity.StockAdjustment;
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
    @PreAuthorize("hasAuthority('STOCK_ADJUSTMENT_CREATE')")
    @PostMapping
    public StockAdjustment create(@RequestBody StockAdjustmentDTO dto) {
        return service.save(dto);
    }

    @PreAuthorize("hasAuthority('STOCK_ADJUSTMENT_VIEW')")
    @GetMapping("/{id}")
    public StockAdjustmentResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }


    @PreAuthorize("hasAuthority('STOCK_ADJUSTMENT_VIEW')")
    @GetMapping
    public List<StockAdjustment> search(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return service.search(location, keyword, dateFilter, fromDate, toDate);
    }
}