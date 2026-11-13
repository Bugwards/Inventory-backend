package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.ReortDto.BalanceResponse;
import com.example.inventoryAuth.DTO.ReortDto.GrnWiseReportResponse;
import com.example.inventoryAuth.DTO.ReortDto.ReOrderResponse;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.ItemGroupRepository;
import com.example.inventoryAuth.Repository.ItemRepository;
import com.example.inventoryAuth.Repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    ItemGroupRepository itemGroupRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    ItemRepository itemRepository;

    // ---------------- BALANCE REPORT ----------------
    public List<BalanceResponse> itemBalanceReport(Location locationName, String groupName){

        if(groupName.equals("All")) {

            List<ItemGroup> itemGroupList = itemGroupRepository.findAll();
            List<Item> itemList = new ArrayList<>();

            for (ItemGroup itemGroup : itemGroupList) {
                itemList.addAll(itemGroup.getItems());
            }

            List<BalanceResponse> balanceResponseList = new ArrayList<>();

            for (Item item : itemList) {

                BalanceResponse balanceResponse = new BalanceResponse(); // FIX

                Long totaleCurrentBalance = 0L;
                Long totaleActualBalance = 0L;
                Long value = 0L;

                List<Stock> stockList = stockRepository
                        .findByItemAndLocationOrderByCreatedAtAsc(item, locationName);

                for (Stock stock : stockList) {
                    totaleCurrentBalance += stock.getCurrentQty(); // FIX
                    totaleActualBalance += stock.getActualQty();
                    value += (long) (stock.getActualQty() * stock.getUnitPrice());
                }

                balanceResponse.setItemName(item.getItemName());
                balanceResponse.setItemCode(item.getItemCode());
                balanceResponse.setDescription(item.getItemDescription());
                balanceResponse.setItemGroupName(item.getItemGroup().getName());
                balanceResponse.setUnit(item.getUnitOfMeasurement());
                balanceResponse.setCurrentBalance(totaleCurrentBalance);
                balanceResponse.setActualBalance(totaleActualBalance);
                balanceResponse.setValue(value);

                balanceResponseList.add(balanceResponse);
            }
            return balanceResponseList;
        }
        else{

            List<ItemGroup> itemGroupList = itemGroupRepository.findByNameContainingIgnoreCase(groupName);
            List<Item> itemList = new ArrayList<>();

            for (ItemGroup itemGroup : itemGroupList) {
                itemList.addAll(itemGroup.getItems());
            }

            List<BalanceResponse> balanceResponseList = new ArrayList<>();

            for (Item item : itemList) {

                BalanceResponse balanceResponse = new BalanceResponse(); // FIX

                Long totaleCurrentBalance = 0L;
                Long totaleActualBalance = 0L;
                Long value = 0L;

                List<Stock> stockList = stockRepository
                        .findByItemAndLocationOrderByCreatedAtAsc(item, locationName);

                for (Stock stock : stockList) {
                    totaleCurrentBalance += stock.getCurrentQty(); // FIX
                    totaleActualBalance += stock.getActualQty();
                    value += (long) (stock.getActualQty() * stock.getUnitPrice());
                }

                balanceResponse.setItemName(item.getItemName());
                balanceResponse.setItemCode(item.getItemCode());
                balanceResponse.setDescription(item.getItemDescription());
                balanceResponse.setItemGroupName(item.getItemGroup().getName());
                balanceResponse.setUnit(item.getUnitOfMeasurement());
                balanceResponse.setCurrentBalance(totaleCurrentBalance);
                balanceResponse.setActualBalance(totaleActualBalance);
                balanceResponse.setValue(value);

                balanceResponseList.add(balanceResponse);
            }
            return balanceResponseList;
        }
    }

    // ---------------- REORDER REPORT ----------------
    public List<ReOrderResponse> reorderBalanceReport(Location locationName, String groupName) {

        if (groupName.equals("All")) {

            List<ItemGroup> itemGroupList = itemGroupRepository.findAll();
            List<Item> itemList = new ArrayList<>();

            for (ItemGroup itemGroup : itemGroupList) {
                itemList.addAll(itemGroup.getItems());
            }

            List<ReOrderResponse> reOrderResponseList = new ArrayList<>();

            for (Item item : itemList) {

                ReOrderResponse reOrderResponse = new ReOrderResponse(); // FIX

                Long totaleCurrentBalance = 0L;
                Long totaleActualBalance = 0L;

                List<Stock> stockList = stockRepository
                        .findByItemAndLocationOrderByCreatedAtAsc(item, locationName);

                for (Stock stock : stockList) {
                    totaleCurrentBalance += stock.getCurrentQty(); // FIX
                    totaleActualBalance += stock.getActualQty();
                }

                reOrderResponse.setItemName(item.getItemName());
                reOrderResponse.setItemCode(item.getItemCode());
                reOrderResponse.setDescription(item.getItemDescription());
                reOrderResponse.setUnit(item.getUnitOfMeasurement());
                reOrderResponse.setCurrentBalance(totaleCurrentBalance);
                reOrderResponse.setActualBalance(totaleActualBalance);
                reOrderResponse.setReorderQuantity(item.getReorderQuantity());
                reOrderResponse.setMinLevelQuantity(item.getMinimumLevel());

                reOrderResponseList.add(reOrderResponse);
            }
            return reOrderResponseList;
        } else {

            List<ItemGroup> itemGroupList = itemGroupRepository.findByNameContainingIgnoreCase(groupName);
            List<Item> itemList = new ArrayList<>();

            for (ItemGroup itemGroup : itemGroupList) {
                itemList.addAll(itemGroup.getItems());
            }

            List<ReOrderResponse> reOrderResponseList = new ArrayList<>();

            for (Item item : itemList) {

                ReOrderResponse reOrderResponse = new ReOrderResponse(); // FIX

                Long totaleCurrentBalance = 0L;
                Long totaleActualBalance = 0L;

                List<Stock> stockList = stockRepository
                        .findByItemAndLocationOrderByCreatedAtAsc(item, locationName);

                for (Stock stock : stockList) {
                    totaleCurrentBalance += stock.getCurrentQty(); // FIX
                    totaleActualBalance += stock.getActualQty();
                }

                reOrderResponse.setItemName(item.getItemName());
                reOrderResponse.setItemCode(item.getItemCode());
                reOrderResponse.setDescription(item.getItemDescription());
                reOrderResponse.setUnit(item.getUnitOfMeasurement());
                reOrderResponse.setCurrentBalance(totaleCurrentBalance);
                reOrderResponse.setActualBalance(totaleActualBalance);
                reOrderResponse.setReorderQuantity(item.getReorderQuantity());
                reOrderResponse.setMinLevelQuantity(item.getMinimumLevel());

                reOrderResponseList.add(reOrderResponse);
            }
            return reOrderResponseList;
        }
    }

    // ---------------- GRN WISE REPORT ----------------
    public List<GrnWiseReportResponse> grnBalanceReport(Location locationName, String groupName){

        if(groupName.equals("All")) {

            List<ItemGroup> itemGroupList = itemGroupRepository.findAll();
            List<Item> itemList = new ArrayList<>();

            for (ItemGroup itemGroup : itemGroupList) {
                itemList.addAll(itemGroup.getItems());
            }

            List<GrnWiseReportResponse> responseList = new ArrayList<>();

            for (Item item : itemList) {

                GrnWiseReportResponse response = new GrnWiseReportResponse(); // FIX

                Long totaleCurrentBalance = 0L;
                Long totaleActualBalance = 0L;
                double value = 0;

                List<Stock> stockList = stockRepository
                        .findByItemAndLocationOrderByCreatedAtAsc(item, locationName);

                for (Stock stock : stockList) {
                    totaleCurrentBalance += stock.getCurrentQty(); // FIX
                    totaleActualBalance += stock.getActualQty();
                    value += stock.getActualQty() * stock.getUnitPrice();
                }

                List<String> grnNumbers = new ArrayList<>();
                List<LocalDate> grnDates = new ArrayList<>();

                for (GRNItem grnItems : item.getGrnItems()) {
                    grnNumbers.add(grnItems.getGrn().getGrnNumber());
                    grnDates.add(grnItems.getGrn().getGrnDate());
                }

                response.setItemName(item.getItemName());
                response.setItemCode(item.getItemCode());
                response.setDescription(item.getItemDescription());
                response.setItemGroupName(item.getItemGroup().getName());
                response.setUnit(item.getUnitOfMeasurement());
                response.setCurrentBalance(totaleCurrentBalance);
                response.setActualBalance(totaleActualBalance);
                response.setValue(value);
                response.setGrnNumber(grnNumbers);
                response.setGrnDate(grnDates);

                responseList.add(response);
            }

            return responseList;

        } else {

            List<ItemGroup> itemGroupList = itemGroupRepository.findByNameContainingIgnoreCase(groupName);
            List<Item> itemList = new ArrayList<>();

            for (ItemGroup itemGroup : itemGroupList) {
                itemList.addAll(itemGroup.getItems());
            }

            List<GrnWiseReportResponse> responseList = new ArrayList<>();

            for (Item item : itemList) {

                GrnWiseReportResponse response = new GrnWiseReportResponse(); // FIX

                Long totaleCurrentBalance = 0L;
                Long totaleActualBalance = 0L;
                double value = 0;

                List<Stock> stockList = stockRepository
                        .findByItemAndLocationOrderByCreatedAtAsc(item, locationName);

                for (Stock stock : stockList) {
                    totaleCurrentBalance += stock.getCurrentQty(); // FIX
                    totaleActualBalance += stock.getActualQty();
                    value += stock.getActualQty() * stock.getUnitPrice();
                }

                List<String> grnNumbers = new ArrayList<>();
                List<LocalDate> grnDates = new ArrayList<>();

                for (GRNItem grnItems : item.getGrnItems()) {
                    grnNumbers.add(grnItems.getGrn().getGrnNumber());
                    grnDates.add(grnItems.getGrn().getGrnDate());
                }

                response.setItemName(item.getItemName());
                response.setItemCode(item.getItemCode());
                response.setDescription(item.getItemDescription());
                response.setItemGroupName(item.getItemGroup().getName());
                response.setUnit(item.getUnitOfMeasurement());
                response.setCurrentBalance(totaleCurrentBalance);
                response.setActualBalance(totaleActualBalance);
                response.setValue(value);
                response.setGrnNumber(grnNumbers);
                response.setGrnDate(grnDates);

                responseList.add(response);
            }

            return responseList;
        }
    }

    public List<String> itemGroupNamesGetting( ) {
        List<ItemGroup> itemGroupList = itemGroupRepository.findAll();
        List<String> itemNameList = new ArrayList<>();

        for (ItemGroup itemGroup : itemGroupList) {
            itemNameList.add(itemGroup.getName());
        }
        return itemNameList;
    }
    }