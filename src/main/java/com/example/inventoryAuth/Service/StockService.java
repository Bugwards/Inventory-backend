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
                          GRNItem grnItem,
                         int qty,
                         double price) {

        Stock stock = new Stock();
        stock.setItem(item);
        stock.setLocation(location);
        stock.setReferenceId(refId);
        stock.setReferenceType(refType);
        stock.setGrnItem(grnItem);
        stock.setCurrentQty(qty);
        stock.setActualQty(qty);
        stock.setUnitPrice(price);

        return stockRepository.save(stock);
    }
    // =========================
    //  GRN-WISE ADJUSTMENT
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
    //  FIFO DEDUCT (TRANSFER / ISSUE)
    // =========================
    public void deductStock(Item item, Location location, int qty) {

        if (item == null || location == null) {
            throw new RuntimeException("Item and location required");
        }

        if (qty <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }

        List<Stock> stocks =
                stockRepository.findByItemAndLocationOrderByCreatedAtAsc(item, location);

        if (stocks.isEmpty()) {
            throw new RuntimeException("No stock available");
        }

        int remaining = qty;

        for (Stock stock : stocks) {

            if (remaining <= 0) break;

            int available = stock.getCurrentQty() != null ? stock.getCurrentQty() : 0;

            if (available <= 0) continue;

            if (available >= remaining) {
                stock.setCurrentQty(available - remaining);
                remaining = 0;
            } else {
                stock.setCurrentQty(0);
                remaining -= available;
            }

            stockRepository.save(stock);
        }

        if (remaining > 0) {
            throw new RuntimeException("Insufficient stock");
        }
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