package com.example.inventoryAuth.DTO.StockTransferDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//this is for get item details for do adjustment

public class SelectedItemResponse {

   private String itemGroupName;
   private String itemCode;
   private String itemName;
   private String itemDescription;

   private List<SelectedItemGrnResponse> grnWiseItemDetails;
}
