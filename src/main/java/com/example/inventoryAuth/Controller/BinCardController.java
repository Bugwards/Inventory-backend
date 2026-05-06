package com.example.inventoryAuth.Controller;


import com.example.inventoryAuth.DTO.BinCardDTO;
import com.example.inventoryAuth.DTO.BinCardItemDTO;
import com.example.inventoryAuth.DTO.BinCardSummaryDTO;
import com.example.inventoryAuth.Service.BinCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/binCard")
public class BinCardController {

    @Autowired
    BinCardService binCardService;

    @GetMapping("/search")
    public List<BinCardItemDTO> searchItems(@RequestParam String itemName) {
        return binCardService.searchItems(itemName);
    }

    @GetMapping("/{itemCode}")
    public BinCardSummaryDTO getSummary(@PathVariable String itemCode){
       return binCardService.calculateTotalInwardAndOutward(itemCode);
    }
}
