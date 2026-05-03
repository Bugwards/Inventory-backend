package com.example.inventoryAuth.Controller;


import com.example.inventoryAuth.DTO.ItemResponseSearchByKeyword;
import com.example.inventoryAuth.DTO.StockTransferDto.SelectedItemResponse;
import com.example.inventoryAuth.DTO.StockTransferDto.StockTransferListResponse;
import com.example.inventoryAuth.DTO.StockTransferDto.StockTransferRequest;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Service.StockTransferService;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocktransfer")
public class StockTransferController {
     @Autowired
     StockTransferService stockTransferservice ;

    @PostMapping
    @PreAuthorize("hasRole('STORE_STAFF')")
   //1
    //to catch and pass stock transfer data
    public String sTransfer(@RequestBody StockTransferRequest stockTransferRequest){

        stockTransferservice.stockTransferSave(stockTransferRequest);
         return "success";
    }
    //to get item list of related entered keyword
    @GetMapping("/getItem")
    public List<ItemResponseSearchByKeyword> findItem(@RequestParam String keyword){
        return stockTransferservice.findItemByName(keyword);

    }
    //to fill item details for selected item
    @GetMapping("/itemDetails")
    public SelectedItemResponse getItemDetails(@RequestParam  String itemCode){
        return stockTransferservice.getItemDetails(itemCode);
    }

    //get Stocktransfer List
   @GetMapping("/getAllRecords")
    public List<StockTransferListResponse> getStockTransferList(@RequestParam int page){
        return stockTransferservice.getStockTransferList(page);
   }

   //get selected stocktransfer
    @GetMapping("/stockTransferRecord")
    public StockTransferRequest getSelectedStockTransferRecord(@RequestParam String transferNo){
       return  stockTransferservice.getSelectedStockTransferRecord(transferNo);
    }

    @PutMapping("/updateStockTransferRecord/{stockTransferNo}")
    @PreAuthorize("hasRole('STORE_STAFF')")
    public String updateStockTransferRecord(@PathVariable String stockTransferNo,@RequestBody StockTransferRequest  stockTransferRequest){
        stockTransferservice.updateStockTransferRecord(stockTransferNo,stockTransferRequest);
        return "successfully Updated";
    }
   //approve the stockTransfer
   @PutMapping("/approveStockTransfer/{stockTransferNo}")
   @PreAuthorize("hasRole('APPROVING_AUTHORITY')")
    public String approveStockTransfer(@PathVariable String stockTransferNo){
        stockTransferservice.approveStockTransfer(stockTransferNo);
        return "successfully Approved Stock Transfer";
   }

   //cansel the stockTrasnfer
   @PutMapping("/canselStockTransfer/{stockTransferNo}")
   @PreAuthorize("hasRole('APPROVING_AUTHORITY')")
    public String cancelStockTransfer(@PathVariable String stockTransferNo){
        stockTransferservice.cancelStockTransfer(stockTransferNo);
        return " Cancelled Stock Transfer";
   }

   //enter cansel msg
   @PutMapping("/getCanselMsg/")
   @PreAuthorize("hasRole('APPROVING_AUTHORITY')")
    public String getCanselMsg(@RequestParam String stockTransferNo,@RequestBody String canselMsg){
       stockTransferservice.getCancelMsg(stockTransferNo,canselMsg);
       return "successfully Cancelled Stock Transfer";

   }

   /*GetMapping
    public Location getCurrentUserLocation(){
        return stockTransferservice.getCurrentUserLocation();
   }*/


}
