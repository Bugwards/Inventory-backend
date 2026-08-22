package com.example.inventoryAuth.Controller;



import com.example.inventoryAuth.DTO.GRNDTO;
import com.example.inventoryAuth.DTO.OpeningStockDTO;
import com.example.inventoryAuth.DTO.OpeningStockResponseDTO;
import com.example.inventoryAuth.Entity.GRN;
import com.example.inventoryAuth.Entity.OpeningStock;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.Status;
import com.example.inventoryAuth.Service.OpeningStockService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opening-stock")
@CrossOrigin(origins = "http://localhost:3000")
public class OpeningStockController {

    private final OpeningStockService service;

    public OpeningStockController(OpeningStockService service) {
        this.service = service;
    }

    // CREATE
    @PreAuthorize("hasAnyRole('STORE_STAFF','SYSTEM_ADMIN')")
    @PostMapping
    public OpeningStock create(@RequestBody OpeningStockDTO dto) {
        return service.save(dto, null);
    }


    // UPDATE
    @PreAuthorize("hasAnyRole('STORE_STAFF','SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public OpeningStock update(@PathVariable Long id,
                               @RequestBody OpeningStockDTO dto) {
        return service.save(dto, id);
    }


    // APPROVE
    @PreAuthorize("hasAnyRole('APPROVING_AUTHORITY','SYSTEM_ADMIN')")
    @PutMapping("/approve/{id}")
    public OpeningStock approve(@PathVariable Long id) {
        return service.approve(id);
    }

    // CANCEL
    @PreAuthorize("hasAnyRole('APPROVING_AUTHORITY','SYSTEM_ADMIN')")
    @PutMapping("/cancel/{id}")
    public OpeningStock cancel(@PathVariable Long id,
                               @RequestParam String reason) {
        return service.cancel(id, reason);
    }

    //VIEW
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public OpeningStockResponseDTO getById(@PathVariable Long id)
    {
        return service.getById(id);
    }

    //SEARCH
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<OpeningStock> search(
            @RequestParam(required = false) Location location,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return service.search(location, status, keyword, dateFilter, fromDate, toDate);
    }
}