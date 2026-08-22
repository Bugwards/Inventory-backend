package com.example.inventoryAuth.Controller;




import com.example.inventoryAuth.DTO.StockViewDTO;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Service.StockViewService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-view")
@CrossOrigin(origins = "http://localhost:3000")
public class StockViewController {

    private final StockViewService service;

    public StockViewController(StockViewService service) {
        this.service = service;
    }

    @GetMapping
    public List<StockViewDTO> getStockView(
            @RequestParam(required = false) Location location
    ) {
        return service.getStockView(location);
    }
}
