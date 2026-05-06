package com.example.inventoryAuth.Service;



import com.example.inventoryAuth.Entity.*;
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
    public Stock addStock(Item item,
                         Location location,
                         Long refId,
                         ReferenceType refType,
                         int qty,
                         double price,
                         GRNItem grnItem) {

        Stock stock = new Stock();
        stock.setItem(item);
        stock.setLocation(location);
        stock.setReferenceId(refId);
        stock.setReferenceType(refType);
        stock.setCurrentQty(qty);
        stock.setActualQty(qty);
        stock.setUnitPrice(price);
        stock.setGrnItem(grnItem);
        if (grnItem != null) {
            stock.setGrnItem(grnItem);
        }

        return stockRepository.save(stock);
    }
    // =========================
    //  STOCK ADJUSTMENT
    // =========================
    public void adjustStockDirect(Stock stock, int qty) {

        int newQty = stock.getCurrentQty() + qty;

        if (newQty < 0) {
            throw new RuntimeException("Not enough stock for reference: " + stock.getReferenceId());
        }

        stock.setCurrentQty(newQty);
        stock.setActualQty(stock.getActualQty() + qty);

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