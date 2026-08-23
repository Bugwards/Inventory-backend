package com.example.inventoryAuth.Controller;


import com.example.inventoryAuth.DTO.ItemResponseSearchByKeyword;
import com.example.inventoryAuth.DTO.StockIssueDto.IssuingItemResponse;
import com.example.inventoryAuth.DTO.StockIssueDto.StockIssueFilterRequest;
import com.example.inventoryAuth.DTO.StockIssueDto.StockIssueRequest;
import com.example.inventoryAuth.DTO.StockIssueDto.StockIssueResponse;
import com.example.inventoryAuth.Entity.Location;
import com.example.inventoryAuth.Service.StockIssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stockIssue")
@EnableMethodSecurity
@CrossOrigin("http://localhost:3000")
public class StockIssueController {

    @Autowired
    StockIssueService stockIssueService;

    @GetMapping
    public Location getCurrentLocation(){
        return stockIssueService.getCurrentUserLocation();
    }

    @PostMapping("/create/issue")
    @PreAuthorize("hasAuthority('STOCK_ISSUE_CREATE')")
    public String addStockIssue(@RequestBody StockIssueRequest stockIssueRequest){
        stockIssueService.createStockIssue(stockIssueRequest);
        return "Stock Issue Created Successfully";
    }

    @GetMapping("/getItemList/{keyword}")
    public List<ItemResponseSearchByKeyword> getItemListByKeyword(@PathVariable String keyword){
        return stockIssueService.getItemByKeyword(keyword);
    }

    @GetMapping("/getItem/{itemCode}/details")
    public IssuingItemResponse getItemDetails(@PathVariable String itemCode){
        return stockIssueService.getItemDetails(itemCode);
    }

    @GetMapping("/getAllStockIssues")
    public List<StockIssueResponse> getAllIssuedRecords(@RequestParam int startPage){
        return stockIssueService.getAllIssuedRecords(startPage);
    }

    @PutMapping("/updateStockIssueReocrd/{stockIssueNo}")
    @PreAuthorize("hasAuthority('STOCK_ISSUE_CREATE')")
    public void updateReocrd(@RequestBody StockIssueRequest stockIssueRequest,@PathVariable String stockIssueNo){
        stockIssueService.updateStockIssueRecord(stockIssueRequest ,stockIssueNo);
    }

    @GetMapping("/issueRecord")
    public StockIssueRequest getSelectedIssueRecord(@RequestParam String issueNo){
        return stockIssueService.getSelectedIssueRecord(issueNo);
    }

    @PutMapping("/approveRecord/{issueNo}")
    @PreAuthorize("hasAuthority('STOCK_ISSUE_APPROVE')")
    public String approveRecord(@PathVariable String issueNo){
        stockIssueService.approveRecord(issueNo);
        return"Approved!";
    }

    @PutMapping("/cancelRecord/{issueNo}")
    @PreAuthorize("hasAuthority('STOCK_ISSUE_APPROVE')")
    public String cancelRecord(@PathVariable String issueNo){
        stockIssueService.cancelRecord(issueNo);
        return"Canceled!";
    }

    @PutMapping("/cancelRecord/checkbox")
    public String saveCancelMassage(@RequestBody String cancelMassage , @RequestParam String issueNo){
        stockIssueService.saveCancelReason(cancelMassage, issueNo);
        return "Message Updated";
    }

    @PostMapping("/filter/issueReocrds")
    public List<StockIssueResponse> filterStockIssues( @RequestBody StockIssueFilterRequest request) {

        return stockIssueService.getFilteredIssues(request);

    }



}
