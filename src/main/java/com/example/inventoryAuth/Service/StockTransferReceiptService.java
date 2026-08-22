package com.example.inventoryAuth.Service;


import com.example.inventoryAuth.DTO.StockReceiptDto.*;
import com.example.inventoryAuth.DTO.StockTransferDto.StockTransferListResponse;
import com.example.inventoryAuth.DTO.StockTransferDto.TransferredGrnResponse;
import com.example.inventoryAuth.DTO.StockTransferDto.TransferredItemResponse;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service

public class StockTransferReceiptService {


    @Autowired
    StockTransferRepository stockTransferRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    GRNItemRepository grnItemRepository;

    @Autowired
    GrnRepository grnRepository;

    @Autowired
    StockReceiptRepository stockReceiptRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StockTransferItemRepository stockTransferItemRepository;

    public Location getCurrentUserLocation() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getLocation();
    }

    public void saveStockReceipt(StockReceiptRequest stockReceipt) {
        StockReceipt stockTransferReceipt = new StockReceipt();
        stockTransferReceipt.setDate(stockReceipt.getDate());
        stockTransferReceipt.setFromLocation(stockReceipt.getFromLocation());
        stockTransferReceipt.setReceiptLocation(stockReceipt.getReciptLocation());//*        stockTransferReceipt.setTransferNo(stockReceipt.getTransferNo());
        stockTransferReceipt.setComment(stockReceipt.getComment());
        StockTransfer stockTransfer = stockTransferRepository.findByTransferNo(stockReceipt.getTransferNo()).orElse(null);
        stockTransfer.setStatus(Status.TRANSFERRED);
        stockTransferReceipt.setStockTransfer(stockTransfer);//save related stransfer with sreceipt

        List<StockReceiptItemsRequest> stockReceiptItemList = stockReceipt.getStockReceiptItems();
        for (StockReceiptItemsRequest stockReceiptItem : stockReceiptItemList) {

            Item item = itemRepository.findByItemCode(stockReceiptItem.getItemCode()).orElseThrow(() -> new RuntimeException("Record Not Found"));//got the item

            List<StockReceiptItemGrnRequest> stockReceiptItemGrnList = stockReceiptItem.getStockReceiptItemGrn();
            for (StockReceiptItemGrnRequest stockReceiptItemGrn : stockReceiptItemGrnList) {
                StockReceiptItem stockTransferReceiptItem = new StockReceiptItem();
                stockTransferReceiptItem.setItem(item);//added item to StockTransferReceiptItem

                GRN grn = grnRepository.findByGrnNumber(stockReceiptItemGrn.getGrnNo());
                GRNItem grnItem = grnItemRepository.findByGrnAndItem(grn, item).orElseThrow(() -> new RuntimeException("Record Not Found"));
                stockTransferReceiptItem.setGrnItem(grnItem);
                stockTransferReceiptItem.setItem(item);
                stockTransferReceiptItem.setReceivedQty(stockReceiptItemGrn.getReceivedQty());
                stockTransferReceiptItem.setStockReceipt(stockTransferReceipt);
                stockTransferReceipt.getStockReceiptItem().add(stockTransferReceiptItem);
            }

        }
        StockReceipt stockTReceipt = stockReceiptRepository.save(stockTransferReceipt);
        String receiptNo = "RCP" + String.format("%06d", stockTReceipt.getId());
        stockTReceipt.setReceiptNo(receiptNo);
        stockReceiptRepository.save(stockTransferReceipt);
    }

    //get items after click on 'populate items' button
    public List<TransferredItemResponse> populateItems(String transferNo) {
        StockTransfer stockTransfer = stockTransferRepository.findByTransferNo(transferNo.trim()).orElseThrow(() -> new RuntimeException("Stock transfer not found: " + transferNo));


        List<TransferredItemResponse> transferredItemDtoList = new ArrayList<>();

        List<StockTransferItem> StockTransferItemList = stockTransfer.getStockTransferItem();

        for (StockTransferItem stockTransferItem : StockTransferItemList) {

            TransferredItemResponse itemDto = new TransferredItemResponse();
            itemDto.setItemGroup(stockTransferItem.getItem().getItemGroup().getName());
            itemDto.setItemCode(stockTransferItem.getItem().getItemCode());
            itemDto.setItemName(stockTransferItem.getItem().getItemName());
            itemDto.setDescription(stockTransferItem.getItem().getItemDescription());
            itemDto.setUnitOfMeasurement(stockTransferItem.getItem().getUnitOfMeasurement());
            itemDto.setTransferQty(stockTransferItem.getTransferQty());

            List<TransferredGrnResponse> grnItemTransferredDtoList = new ArrayList<>();
            List<GRNItem> grnItemList = stockTransferItem.getItem().getGrnItems();
            for (GRNItem grnItem : grnItemList) {
                TransferredGrnResponse grnItemDto = new TransferredGrnResponse();
                grnItemDto.setGrnNumber(grnItem.getGrn().getGrnNumber());
                grnItemDto.setGrnDate(grnItem.getGrn().getGrnDate());
                grnItemDto.setTransferQty(stockTransferItem.getTransferQty());

                grnItemTransferredDtoList.add(grnItemDto);
            }
            itemDto.setTranferredgrnitem(grnItemTransferredDtoList);
            transferredItemDtoList.add(itemDto);
        }

        return transferredItemDtoList;

    }
    //get stock transfer list
    public List<StockTransferListResponse> getStockTransferList(int page, Location fromLocation, Location receiptLocation) {
        List<StockTransfer> stockTransferList = stockTransferRepository.findByFromLocationAndToLocationAndStatus(fromLocation, receiptLocation, Status.APPROVED, PageRequest.of(page, 5));
        List<StockTransferListResponse> stockTransferListResponse = new ArrayList<>();
        for (StockTransfer stockTransfer : stockTransferList) {
            StockTransferListResponse sTransferResponse = new StockTransferListResponse();
            sTransferResponse.setTransferNo(stockTransfer.getTransferNo());
            sTransferResponse.setTransferDate(stockTransfer.getTransferDate());
            sTransferResponse.setFromLocation(stockTransfer.getFromLocation());
            sTransferResponse.setToLocation(stockTransfer.getToLocation());
            sTransferResponse.setStatus(stockTransfer.getStatus());
            sTransferResponse.setApprovedDate(stockTransfer.getApprovedDate());
            stockTransferListResponse.add(sTransferResponse);
        }
        return stockTransferListResponse;
    }


    //get StockTransferReceipt list
    public List<StockReceiptListResponse> getStockTransferReceiptList(int page) {
        Page<StockReceipt> stockTransferReceipts = stockReceiptRepository.findAll(PageRequest.of(page, 5));
        List<StockReceiptListResponse> stockTransferReceiptList = new ArrayList<>();

        for (StockReceipt receipt : stockTransferReceipts) {
            StockReceiptListResponse receiptResponse = new StockReceiptListResponse();
            receiptResponse.setReceiptNo(receipt.getReceiptNo());
            receiptResponse.setReceiptDate(receipt.getDate());
            receiptResponse.setTransferNo(receipt.getTransferNo());
            receiptResponse.setFromLocation(receipt.getFromLocation());
            receiptResponse.setReceiptLocation(receipt.getReceiptLocation());
            receiptResponse.setStatus(receipt.getStatus());
            receiptResponse.setApprovedDate(receipt.getApprovedDate());

            stockTransferReceiptList.add(receiptResponse);
        }
        return stockTransferReceiptList;
    }

    //get a stockreceiptRecord clicking on str via str list
    public StockReceiptRequest getSelectedStockReceipt(String receiptNo) {
        StockReceipt stockReceipt = stockReceiptRepository.findByReceiptNo(receiptNo);

        StockReceiptRequest stockReceiptRequest = new StockReceiptRequest();
        stockReceiptRequest.setDate(stockReceipt.getDate());
        stockReceiptRequest.setFromLocation(stockReceipt.getFromLocation());
        stockReceiptRequest.setReciptLocation(stockReceipt.getReceiptLocation());
        stockReceiptRequest.setTransferNo(stockReceipt.getTransferNo());
        stockReceiptRequest.setComment(stockReceipt.getComment());

        Integer totalReceivedQuantity = 0;
        Long totalTransferedQuantity = 0L;

        List<StockReceiptItemsRequest> ReceiptItemRequestList = new ArrayList<>();
        List<StockReceiptItem> ReceiptItemList = stockReceipt.getStockReceiptItem();
        for (StockReceiptItem receiptItem : ReceiptItemList) {
            StockReceiptItemsRequest itemRequest = new StockReceiptItemsRequest();
            itemRequest.setItemGroup(receiptItem.getItem().getItemGroup().getName());
            itemRequest.setItemCode(receiptItem.getItem().getItemCode());
            itemRequest.setItemName(receiptItem.getItem().getItemName());
            itemRequest.setDescription(receiptItem.getItem().getItemDescription());
            itemRequest.setUnitOfMeasurement(receiptItem.getItem().getUnitOfMeasurement());

            List<StockReceiptItemGrnRequest> StockReceiptItemGrnRequestList = new ArrayList<>();
            List<GRNItem> grnItemList = receiptItem.getItem().getGrnItems();//get grnItemList
            for (GRNItem grnItem : grnItemList) {
                StockReceiptItemGrnRequest itemGrnRequest = new StockReceiptItemGrnRequest();
                itemGrnRequest.setGrnNo(grnItem.getGrn().getGrnNumber());
                itemGrnRequest.setGrnDate(grnItem.getGrn().getGrnDate());
                StockTransferItem stockTransferItem = stockTransferItemRepository.findByStockTransferAndItem(stockReceipt.getStockTransfer(), receiptItem.getItem());
                itemGrnRequest.setTransferredQty(Long.valueOf(stockTransferItem.getTransferQty()));
                itemGrnRequest.setReceivedQty(receiptItem.getReceivedQty());

                totalReceivedQuantity = totalReceivedQuantity + itemGrnRequest.getReceivedQty();
                totalTransferedQuantity = totalTransferedQuantity + itemGrnRequest.getTransferredQty();

                StockReceiptItemGrnRequestList.add(itemGrnRequest);
            }
            itemRequest.setStockReceiptItemGrn(StockReceiptItemGrnRequestList);
            itemRequest.setTransferredQty(totalTransferedQuantity);
            itemRequest.setReceivedQty(totalReceivedQuantity);
            ReceiptItemRequestList.add(itemRequest);
        }
        stockReceiptRequest.setStockReceiptItems(ReceiptItemRequestList);
        return stockReceiptRequest;

    }

    //update stockReceipt
    public void updateStockReceipt(String receiptNo, StockReceiptRequest stockReceiptRequest) {
        StockReceipt stockReceipt = stockReceiptRepository.findByReceiptNo(receiptNo);
        if (stockReceipt.getStatus() == Status.APPROVED || stockReceipt.getStatus() == Status.CANCELLED) {
            throw new RuntimeException("You Can't Update this stock transfer record");
        }
        stockReceipt.setDate(stockReceiptRequest.getDate());
        stockReceipt.setFromLocation(stockReceiptRequest.getFromLocation());
        stockReceipt.setTransferNo(stockReceiptRequest.getTransferNo());
        stockReceipt.setComment(stockReceiptRequest.getComment());


        List<StockReceiptItemsRequest> stockReceiptItemList = stockReceiptRequest.getStockReceiptItems();
        for (StockReceiptItemsRequest stockReceiptItem : stockReceiptItemList) {

            Item item = itemRepository.findByItemCode(stockReceiptItem.getItemCode()).orElseThrow(() -> new RuntimeException("Record Not Found"));

            List<StockReceiptItemGrnRequest> stockReceiptItemGrnList = stockReceiptItem.getStockReceiptItemGrn();
            for (StockReceiptItemGrnRequest stockReceiptItemGrn : stockReceiptItemGrnList) {
                StockReceiptItem ReceiptItem = new StockReceiptItem();
                ReceiptItem.setItem(item);

                GRN grn = grnRepository.findByGrnNumber(stockReceiptItemGrn.getGrnNo());
                GRNItem grnItem = grnItemRepository.findByGrnAndItem(grn, item).orElseThrow(() -> new RuntimeException("Record Not Found"));
//                grnItem.setGrnWiseReceivedQuantity(stockReceiptItemGrn.getReceivedQty());
                ReceiptItem.setGrnItem(grnItem);
                ReceiptItem.setItem(item);
                ReceiptItem.setReceivedQty(stockReceiptItemGrn.getReceivedQty());
                ReceiptItem.setStockReceipt(stockReceipt);
                stockReceipt.getStockReceiptItem().add(ReceiptItem);
            }

        }
        stockReceiptRepository.save(stockReceipt);
    }

    //approve stock receipt
    public void approveStockReceipt(String receiptNo) {
        StockReceipt stockReceipt = stockReceiptRepository.findByReceiptNo(receiptNo);
        stockReceipt.setStatus(Status.APPROVED);

        List<StockReceiptItem> stockReceiptItemList = stockReceipt.getStockReceiptItem();
        for (StockReceiptItem stockReceiptItem : stockReceiptItemList) {
            Stock stock = stockRepository.findByItemAndGrnItem(stockReceiptItem.getItem(), stockReceiptItem.getGrnItem());
            stock.setCurrentQty(stock.getCurrentQty() + stockReceiptItem.getReceivedQty());
            stock.setActualQty(stock.getActualQty() + stockReceiptItem.getReceivedQty());
        }
        stockReceiptRepository.save(stockReceipt);
    }

    //cansel stock receipt
    public void cancelStockReceipt(String receiptNo) {
        StockReceipt stockReceipt = stockReceiptRepository.findByReceiptNo(receiptNo);
        stockReceipt.setStatus(Status.CANCELLED);
        stockReceiptRepository.save(stockReceipt);

    }

    //cansel msg reason
    public void getCancelMsg(String receiptNo, String canselReason) {
        StockReceipt stockReceipt = stockReceiptRepository.findByReceiptNo(receiptNo);
        stockReceipt.setCancelReason(canselReason);
        stockReceiptRepository.save(stockReceipt);

    }
}
