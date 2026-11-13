package com.example.inventoryAuth.Controller;

import com.example.inventoryAuth.DTO.StockReceiptDto.StockReceiptListResponse;
import com.example.inventoryAuth.DTO.StockReceiptDto.StockReceiptRequest;
import com.example.inventoryAuth.DTO.StockTransferDto.StockTransferListResponse;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Service.StockTransferReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/StockReceipt")
public class StockReceiptController {

    @Autowired
    StockTransferReceiptService stockTransferReceiptService;

    @GetMapping("/currentUserLocation")
    public Location getCurrentUserLocation() {
        return stockTransferReceiptService.getCurrentUserLocation();
    }

    @PostMapping
    public String stockReceiption(@RequestBody StockReceiptRequest stockReceipt) {
        stockTransferReceiptService.saveStockReceipt(stockReceipt);
        return "successfully saved StockReceipt";
    }

    @GetMapping
    public List<StockTransferListResponse> getStockTransferList(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(required = false) String fromLocation,
                                                                 @RequestParam(required = false) String receiptLocation) {
        return stockTransferReceiptService.getStockTransferList(page, fromLocation, receiptLocation);
    }

    @GetMapping("/getAllReceipts")
    public List<StockReceiptListResponse> getStockTransferReceiptList(@RequestParam(defaultValue = "0") int page) {
        return stockTransferReceiptService.getStockTransferReceiptList(page);
    }

    @GetMapping("/stockReceiptRecord")
    public StockReceiptRequest getSelectedStockReceipt(@RequestParam String receiptNo) {
        return stockTransferReceiptService.getSelectedStockReceipt(receiptNo);
    }

    @PutMapping
    public String stockReceiptUpdate(@RequestParam String receiptNo, @RequestBody StockReceiptRequest stockReceiptRequest) {
        stockTransferReceiptService.updateStockReceipt(receiptNo, stockReceiptRequest);
        return "successfully Updated";
    }

    @PutMapping("/approveReceipt")
    public String approveStockReceipt(@RequestParam String receiptNo) {
        stockTransferReceiptService.approveStockReceipt(receiptNo);
        return "successfully Approved Stock Transfer";
    }

    @PutMapping("/canselReceipt")
    public String cancelStockReceipt(@RequestParam String receiptNo,String reason) {
        stockTransferReceiptService.cancelStockReceipt(receiptNo,reason);
        return "successfully Cancelled Stock Receipt";
    }
}
