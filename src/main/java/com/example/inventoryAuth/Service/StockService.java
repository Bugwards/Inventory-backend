package com.example.inventoryAuth.Service;



import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Entity.Stock;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Entity.ReferenceType;
import com.example.inventoryAuth.Repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    // =========================
    // ADD STOCK (GRN / OPENING)
    // =========================
    public void addStock(Item item,
                         Location location,
                         Long refId,
                         ReferenceType refType,
                         int qty,
                         double price) {

        Stock stock = new Stock();
        stock.setItem(item);
        stock.setLocation(location);
        stock.setReferenceId(refId);
        stock.setReferenceType(refType);
        stock.setCurrentQty(qty);
        stock.setActualQty(qty);
        stock.setUnitPrice(price);

        stockRepository.save(stock);
    }
    // =========================
    //  GET ALL STOCK
    // =========================
    public List<Stock> getAllStock() {
        return stockRepository.findAll();
    }

    // =========================
    //  GET BY LOCATION
    // =========================
    public List<Stock> getByLocation(Location location) {
        return stockRepository.findByLocation(location);
    }

    // =========================
    // GET BY ITEM
    // =========================
    public List<Stock> getByItem(Long itemId) {
        return stockRepository.findByItemId(itemId);
    }

    // =========================
    // GET STOCK (FIFO / POPUP)
    // =========================
    public List<Stock> getStock(Item item, Location location) {
        return stockRepository
                .findByItemAndLocationOrderByCreatedAtAsc(item, location);
    }
}