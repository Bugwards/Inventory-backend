package com.example.inventoryAuth.Service;



import com.example.inventoryAuth.DTO.StockViewDTO;
import com.example.inventoryAuth.Entity.Stock;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockViewService {

    private final StockRepository stockRepository;

    public StockViewService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // =========================
    // STOCK VIEW (UI / REPORTING)
    // =========================
    public List<StockViewDTO> getStockView(Location location) {

        List<Stock> stocks = stockRepository.findAll();

        // FILTER BY LOCATION
        if (location != null) {
            stocks = stocks.stream()
                    .filter(s -> s.getLocation() == location)
                    .toList();
        }

        List<StockViewDTO> result = new ArrayList<>();

        for (Stock s : stocks) {

            StockViewDTO dto = new StockViewDTO();

            dto.setStockId(s.getStockId());

            // ITEM INFO
            dto.setItemCode(s.getItem().getItemCode());
            dto.setItemName(s.getItem().getItemName());

            // GRN INFO (REFERENCE)
            dto.setGrnId(s.getReferenceId());
            dto.setGrnNo(
                    s.getReferenceType() != null
                            ? s.getReferenceType().name() + "-" + s.getReferenceId()
                            : null
            );

            // LOCATION
            dto.setLocation(s.getLocation());

            // QUANTITY INFO
            dto.setCurrentQty(s.getCurrentQty());
            dto.setActualQty(s.getActualQty());

            // PRICE
            dto.setUnitPrice(s.getUnitPrice());

            // DATE
            dto.setCreatedAt(s.getCreatedAt());

            // CALCULATED FIELD (optional frontend use)
            dto.setAdjustedQty(
                    (s.getActualQty() != null && s.getCurrentQty() != null)
                            ? (s.getActualQty() - s.getCurrentQty())
                            : 0
            );

            result.add(dto);
        }

        return result;
    }
}