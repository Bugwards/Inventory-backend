package com.example.inventoryAuth.Controller;


import com.example.inventoryAuth.DTO.StockTransferDto.TransferredItemResponse;
import com.example.inventoryAuth.Service.StockTransferReceiptService;
import com.example.inventoryAuth.DTO.StockReceiptDto.StockReceiptListResponse;
import com.example.inventoryAuth.DTO.StockReceiptDto.StockReceiptRequest;
import com.example.inventoryAuth.DTO.StockTransferDto.StockTransferListResponse;
import com.example.inventoryAuth.Entity.Location;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/StockReceipt")

public class StockReceiptController {

    @Autowired
    StockTransferReceiptService stockTransferReceiptService;
   //save stock transfer receipt
   @PreAuthorize("hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
   @PostMapping("/createRecipt")
    public String stockReceiption(@RequestBody StockReceiptRequest stockReceipt){
        stockTransferReceiptService.saveStockReceipt(stockReceipt);
        return "successfully saved StockReceipt";
    }

    //get stock transfer list
    @GetMapping("/getTransferList")
    public List<StockTransferListResponse> getStockTransferList(@RequestParam int page, @RequestParam Location fromLocation, @RequestParam Location receiptLocation){
        return stockTransferReceiptService.getStockTransferList(page,fromLocation,receiptLocation);
    }

    //get stock transfr receipt list
    @GetMapping("/getAllReceipts")
    public List<StockReceiptListResponse> getStockTransferReceiptList(@RequestParam int page){
        return stockTransferReceiptService.getStockTransferReceiptList(page);
    }

    //get selected stock receipt
    @GetMapping("/stockReceiptRecord")
    public StockReceiptRequest getSelectedStockReceipt(@RequestParam String receiptNo){
        return stockTransferReceiptService.getSelectedStockReceipt(receiptNo);

    }

    //update receipt
    @PreAuthorize("hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @PutMapping("/receiptUpdate")
    public String stockReceiptUpdate(@RequestParam String receiptNo,@RequestBody StockReceiptRequest stockReceiptRequest){
        stockTransferReceiptService.updateStockReceipt(receiptNo,stockReceiptRequest);
        return "successfully Updated";
    }
    //approve receipt
    @PreAuthorize("hasAuthority('ROLE_APPROVING_AUTHORITY') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @PutMapping("/approveReceipt")
    public String approveStockReceipt(@RequestParam String receiptNo){
        stockTransferReceiptService.approveStockReceipt(receiptNo);
        return "successfully Approved Stock Transfer";
    }

    @PreAuthorize("hasAuthority('ROLE_APPROVING_AUTHORITY') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @PutMapping("/canselReceipt")
    public String cancelStockReceipt(@RequestParam String receiptNo){
        stockTransferReceiptService.cancelStockReceipt(receiptNo);
        return "successfully Cancelled Stock receipt";
    }

    @PreAuthorize("hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @PutMapping("/cancelReason")
    public String saveCancelReason(@RequestParam String receiptNo, @RequestBody String reason) {
        stockTransferReceiptService.getCancelMsg(receiptNo, reason);
        return "Cancel reason saved";
    }

    @PreAuthorize("hasAuthority('ROLE_STORE_STAFF') or hasAuthority('ROLE_SYSTEM_ADMIN')")
    @GetMapping("/createRecipt/getTransferItems")
    public List<TransferredItemResponse> populateItems(@RequestParam String transferNo){
       return stockTransferReceiptService.populateItems(transferNo);
    }

    @GetMapping("/currentUserLocation")
    public Location getCurrentUserLocation() {
        return stockTransferReceiptService.getCurrentUserLocation();
    }


}
