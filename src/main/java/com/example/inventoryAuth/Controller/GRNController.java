package com.example.inventoryAuth.Controller;


import com.example.inventoryAuth.DTO.GRNDTO;
import com.example.inventoryAuth.DTO.GRNResponseDTO;
import com.example.inventoryAuth.Entity.GRN;
import com.example.inventoryAuth.Entity.Status;
import com.example.inventoryAuth.Service.GRNService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grn")
@CrossOrigin(origins = "http://localhost:3000")
public class GRNController {

    private final GRNService service;

    public GRNController(GRNService service) {

        this.service = service;
    }

    //create
    @PreAuthorize("hasAuthority('GRN_CREATE')")
    @PostMapping
    public GRN create(@RequestBody GRNDTO dto) {

        return service.save(dto, null);
    }


    //update
    @PreAuthorize("hasAuthority('GRN_CREATE')")
    @PutMapping("/{id}")
    public GRN update(@PathVariable Long id, @RequestBody GRNDTO dto) {

        return service.save(dto, id);
    }
//approve
    @PreAuthorize("hasAuthority('GRN_APPROVE')")
    @PutMapping("/approve/{id}")
    public GRN approve(@PathVariable Long id) {
        return service.approve(id);
    }
//cancel
@PreAuthorize("hasAuthority('GRN_APPROVE')")
    @PutMapping("/cancel/{id}")
    public GRN cancel(@PathVariable Long id, @RequestParam String reason) {

        return service.cancel(id, reason);
    }

    //view
    @PreAuthorize("hasAuthority('GRN_VIEW')")
    @GetMapping("/{id}")
    public GRNResponseDTO getById(@PathVariable Long id) {

        return service.getById(id);
    }

    //search
    @PreAuthorize("hasAuthority('GRN_VIEW')")
    @GetMapping
    public List<GRNResponseDTO> search(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dateFilter,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return service.search(
                location,
                status,
                keyword,
                dateFilter,
                fromDate,
                toDate);
    }
}
