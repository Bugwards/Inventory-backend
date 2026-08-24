package com.example.inventoryAuth.Service;


import com.example.inventoryAuth.DTO.ItemResponseSearchByKeyword;
import com.example.inventoryAuth.DTO.StockTransferDto.*;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class  StockTransferService {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    GrnRepository grnRepository;

    @Autowired
    GRNItemRepository grnItemRepository;

    @Autowired
    StockTransferRepository stockTransferRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    ItemGroupRepository itemGroupRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BinCardService binCardService;

    @Autowired
    LocationRepository locationRepository;

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

    //get currently logged in nusername for appear in approval and cansellation
    private String getCurrentUsername() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }

        return principal.toString();
    }

    @Transactional
    //save stockTransfer
    public void stockTransferSave(StockTransferRequest stockTransferRequestDetails){

        StockTransfer stockTransfer = new StockTransfer();
        stockTransfer.setTransferDate(stockTransferRequestDetails.getDate());
        stockTransfer.setToLocation(locationRepository.findById(stockTransferRequestDetails.getToLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found")));
        stockTransfer.setRequestRef(stockTransferRequestDetails.getRequestRef());
        stockTransfer.setComment(stockTransferRequestDetails.getComment());
        stockTransfer.setFromLocation(getCurrentUserLocation());
        stockTransfer.setStatus(Status.UNAPPROVED);

        List<TransferredItemResponse> trasferredItemDtoList = stockTransferRequestDetails.getItems();

        for(TransferredItemResponse itemDto:trasferredItemDtoList){

            Item item = itemRepository.findByItemCode(itemDto.getItemCode()).orElseThrow(()-> new RuntimeException("Item not found"));

            for(TransferredGrnResponse grnDto:itemDto.getTranferredgrnitem()){
                StockTransferItem stockTransferItem = new StockTransferItem();
                GRN grn = grnRepository.findByGrnNumber(grnDto.getGrnNumber());//to find grn
                GRNItem grnItem= grnItemRepository.findByGrnAndItem(grn,item).orElse(null);//to find grnitem from grn
                grnItem.setGrnWiseTransferredQuantity(grnDto.getTransferQty());
                Stock stock = stockRepository.findByItemAndGrnItem(item, grnItem);
                if(stock == null){
                    throw new RuntimeException("Stock not found for item + GRN");
                }
                if(stock.getCurrentQty() < grnDto.getTransferQty()) {
                    throw new RuntimeException(
                            "Not enough stock in GRN: " +
                                    stock.getGrnItem().getGrn().getGrnNumber()
                    );
                }
                //setting stocktransferItem data
                stockTransferItem.setTransferQty(itemDto.getTransferQty());
                stockTransferItem.setStockTransfer(stockTransfer);
                stockTransferItem.setItem(item);
                stockTransferItem.setGrnItem(grnItem);
                //add stocktransferItem to stockTransfer
                stockTransfer.getStockTransferItem().add(stockTransferItem);
                //reduce transQty from Current to get new CurrntQty
                stock.setCurrentQty(stock.getCurrentQty() - grnDto.getTransferQty());

            }


        }

        StockTransfer savedSTransfer=stockTransferRepository.save(stockTransfer);//save stockTransfer to DB
        //generate and save trs no
        String TransferNo = "TRS" + String.format("%06d", savedSTransfer.getId());
        savedSTransfer.setTransferNo(TransferNo);//set transNO for created transfer

        stockTransferRepository.save(savedSTransfer);

    }

    //get item details by keyword after press search button
    public List<ItemResponseSearchByKeyword>  findItemByName(String keyword){
        List<Item> itemList =
                itemRepository.findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCaseOrItemDescriptionContainingIgnoreCase(
                        keyword,
                        keyword,
                        keyword
                );
        List<ItemResponseSearchByKeyword> itemDtoList = new ArrayList<>();
        for (Item item:itemList){
            ItemResponseSearchByKeyword itemDto = new ItemResponseSearchByKeyword();
            itemDto.setItemCode(item.getItemCode());
            itemDto.setItemName(item.getItemName());
            itemDto.setDescription(item.getItemDescription());
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }


    //to get item details after enter keyword and click on selected item
    public SelectedItemResponse getItemDetails(String itemCode){
        Item selectedItem=itemRepository.findByItemCode(itemCode).orElseThrow(()->new RuntimeException("Item not found"));
        SelectedItemResponse itemResponse = new SelectedItemResponse();
        itemResponse.setItemGroupName(selectedItem.getItemGroup() != null ? selectedItem.getItemGroup().getName() : "");
        itemResponse.setItemCode(selectedItem.getItemCode());
        itemResponse.setItemName(selectedItem.getItemName());
        itemResponse.setItemDescription(selectedItem.getItemDescription());

        //get grn list
        List<SelectedItemGrnResponse> responseGrnList = new ArrayList<>();

        List<Stock> stock = stockRepository.findByItemId(selectedItem.getId());

        if (stock != null) {
            for(Stock stockItem: stock){
                SelectedItemGrnResponse grnWiseItemDetails = new SelectedItemGrnResponse();
                if (stockItem.getGrnItem() != null && stockItem.getGrnItem().getGrn() != null) {
                    grnWiseItemDetails.setGrnNumber(stockItem.getGrnItem().getGrn().getGrnNumber());
                    grnWiseItemDetails.setGrnDate(stockItem.getGrnItem().getGrn().getGrnDate());
                } else {
                    grnWiseItemDetails.setGrnNumber("GRN-N/A");
                    grnWiseItemDetails.setGrnDate(null);
                }
                grnWiseItemDetails.setUnit(selectedItem.getUnitOfMeasurement());
                grnWiseItemDetails.setCurrentQuantity(Long.valueOf(stockItem.getCurrentQty()));
                grnWiseItemDetails.setPricePerUnit(stockItem.getUnitPrice());

                //add data for responseGrnList
                responseGrnList.add(grnWiseItemDetails);
            }
        }

        itemResponse.setGrnWiseItemDetails(responseGrnList);

        return itemResponse;
    }

    //get transfer List
    public List<StockTransferListResponse> getStockTransferList(int page) {
        Page<StockTransfer> StockTransferRecord = stockTransferRepository.findAll(PageRequest.of(page, 5));
        List<StockTransferListResponse> stockTransferList = new ArrayList<>();

        for (StockTransfer Stock : StockTransferRecord) {
            StockTransferListResponse stockTransferResponse = new StockTransferListResponse();
            stockTransferResponse.setTransferNo(Stock.getTransferNo());
            stockTransferResponse.setTransferDate(Stock.getTransferDate());
            stockTransferResponse.setFromLocation(Stock.getFromLocation());
            stockTransferResponse.setToLocation(Stock.getToLocation());
            stockTransferResponse.setStatus(Stock.getStatus());
            stockTransferResponse.setApprovedDate(Stock.getApprovedDate());

            stockTransferList.add(stockTransferResponse);
        }
        return stockTransferList;
    }



    //get and edit stockTransferRecord via transfer List
    public StockTransferRequest getSelectedStockTransferRecord(String transferNo){
        //find stock of related stockTransfer
        StockTransfer stockTransfer=stockTransferRepository.findByTransferNo(transferNo).orElse(null);

        if(stockTransfer==null){
            throw new RuntimeException("stock transfer not found");
        }
        StockTransferRequest transferResponse = new StockTransferRequest();
        transferResponse.setDate(stockTransfer.getTransferDate());
        transferResponse.setToLocationId(stockTransfer.getToLocation() != null ? stockTransfer.getToLocation().getId() : null);
        transferResponse.setRequestRef(stockTransfer.getRequestRef());
        transferResponse.setComment(stockTransfer.getComment());

        transferResponse.setApprovedBy(stockTransfer.getApprovedBy());
        transferResponse.setApprovedAt(stockTransfer.getApprovedAt());

        transferResponse.setCancelledBy(stockTransfer.getCancelledBy());
        transferResponse.setCancelledAt(stockTransfer.getCancelledAt());

        List<TransferredItemResponse> TransferredItemDtoList = new ArrayList<>();
        List<StockTransferItem> StockTransferItemList = stockTransfer.getStockTransferItem();

        for(StockTransferItem stockTransferItem:StockTransferItemList){
            TransferredItemResponse  itemDto = new TransferredItemResponse();
            itemDto.setItemGroup(stockTransferItem.getItem().getItemGroup().getName());
            itemDto.setItemCode(stockTransferItem.getItem().getItemCode());
            itemDto.setItemName(stockTransferItem.getItem().getItemName());
            itemDto.setDescription(stockTransferItem.getItem().getItemDescription());
            itemDto.setUnitOfMeasurement(stockTransferItem.getItem().getUnitOfMeasurement());
            itemDto.setTransferQty(stockTransferItem.getTransferQty());

            List<TransferredGrnResponse>  grnItemTransferredDtoList = new ArrayList<>();
            List<GRNItem> grnItemList = stockTransferItem.getItem().getGrnItems();
            for(GRNItem grnItem:grnItemList){
                //to get currentQ we have to access stock
                Stock stock = stockRepository.findByItemAndGrnItem(stockTransferItem.getItem(),grnItem);

                if (stock == null) {
                    // No stock was ever received against this GRN item, so there's
                    // nothing to report for it here - skip rather than NPE.
                    continue;
                }

                TransferredGrnResponse grnItemDto= new TransferredGrnResponse();
                grnItemDto.setGrnNumber(grnItem.getGrn().getGrnNumber());
                grnItemDto.setGrnDate(grnItem.getGrn().getGrnDate());
                grnItemDto.setCurrentQuantity(stock.getCurrentQty());
                grnItemDto.setTransferQty(stockTransferItem.getGrnItem().getGrnWiseTransferredQuantity());

                grnItemTransferredDtoList.add(grnItemDto);

            }

            itemDto.setTranferredgrnitem(grnItemTransferredDtoList);
            TransferredItemDtoList.add(itemDto);

        }

        transferResponse.setItems(TransferredItemDtoList);
        return transferResponse;
    }

    //save edited details
    public void updateStockTransferRecord(String stockTransferNo,StockTransferRequest  stockTransferRequestDetails){
        StockTransfer stockTransfer=stockTransferRepository.findByTransferNo(stockTransferNo).orElseThrow(()->new RuntimeException("stock transfer not found"));
        if(stockTransfer.getStatus()== Status.APPROVED || stockTransfer.getStatus()==Status.CANCELLED){
            throw new RuntimeException("You Can't Update this stock transfer record");
        }
        stockTransfer.setTransferDate(stockTransferRequestDetails.getDate());
        stockTransfer.setToLocation(locationRepository.findById(stockTransferRequestDetails.getToLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found")));
        stockTransfer.setRequestRef(stockTransferRequestDetails.getRequestRef());
        stockTransfer.setComment(stockTransferRequestDetails.getComment());
        stockTransfer.setFromLocation(locationRepository.findByCode("HEAD_OFFICE")
                .orElseThrow(() -> new RuntimeException("Location not found")));

        for(TransferredItemResponse itemDto:stockTransferRequestDetails.getItems()){
            Item item = itemRepository.findByItemCode(itemDto.getItemCode()).orElseThrow(()->new RuntimeException("item code not found"));

            for(TransferredGrnResponse grnDto:itemDto.getTranferredgrnitem()){
                StockTransferItem stockTransferItem = new StockTransferItem();
                GRN grn = grnRepository.findByGrnNumber(grnDto.getGrnNumber());
                GRNItem grnItem= grnItemRepository.findByGrnAndItem(grn,item).orElse(null);
                grnItem.setGrnWiseTransferredQuantity(grnDto.getTransferQty());
                Stock stock = stockRepository.findByItemAndGrnItem(item, grnItem);
                if(stock == null){
                    throw new RuntimeException("Stock not found for item + GRN");
                }
                if(stock.getCurrentQty() < grnDto.getTransferQty()) {
                    throw new RuntimeException(
                            "Not enough stock in GRN: " +
                                    stock.getGrnItem().getGrn().getGrnNumber()
                    );
                }
                stockTransferItem.setTransferQty(itemDto.getTransferQty());
                stockTransferItem.setStockTransfer(stockTransfer);
                stockTransferItem.setItem(item);
                stockTransferItem.setGrnItem(grnItem);
                //add stocktransferItem to stockTransfer
                stockTransfer.getStockTransferItem().add(stockTransferItem);
                //reduce transQty from Current to get new CurrntQty
                stock.setCurrentQty(stock.getCurrentQty() - grnDto.getTransferQty());
            }
        }

        StockTransfer savedSTransfer=stockTransferRepository.save(stockTransfer);//save stockTransfer to DB
        stockTransferRepository.save(savedSTransfer);

    }

    //approve and update actual quantity
    public void approveStockTransfer(String stockTransferNo){
        StockTransfer stockTransfer=stockTransferRepository.findByTransferNo(stockTransferNo).orElse(null);
        stockTransfer.setStatus(Status.APPROVED);
        stockTransfer.setApprovedDate(java.time.LocalDate.now());
        stockTransfer.setApprovedBy(getCurrentUsername());
        stockTransfer.setApprovedAt(LocalDateTime.now());


        List<StockTransferItem> StocktransferItemList = stockTransfer.getStockTransferItem();

        for(StockTransferItem stockTItem:StocktransferItemList){
            Stock stock=stockRepository.findByItemAndGrnItem(stockTItem.getItem(),stockTItem.getGrnItem());
            stock.setActualQty(stock.getActualQty()-stockTItem.getTransferQty());

            binCardService.createBinCardFromStockTransfer(
                    stock,
                    stockTransfer,
                    stockTItem
            );
        }

        stockTransferRepository.save(stockTransfer);

    }
    //Cansel stock transfer
    public void cancelStockTransfer(String stockTransferNo){
        StockTransfer stockTransfer=stockTransferRepository.findByTransferNo(stockTransferNo).orElse(null);
        stockTransfer.setStatus(Status.CANCELLED);
        stockTransfer.setCancelledBy(getCurrentUsername());
        stockTransfer.setCancelledAt(LocalDateTime.now());


        List<StockTransferItem> StocktransferItemList=stockTransfer.getStockTransferItem();
        for(StockTransferItem StockTItem:StocktransferItemList){
            Stock stock=stockRepository.findByItemAndGrnItem(StockTItem.getItem(),StockTItem.getGrnItem());
            stock.setCurrentQty(stock.getCurrentQty()+StockTItem.getTransferQty());
        }
        stockTransferRepository.save(stockTransfer);
    }

    //get cancell msg for StockCancelling
    public void getCancelMsg(String stockTransferNo,String canselReason){
        StockTransfer stockTransfer= stockTransferRepository.findByTransferNo(stockTransferNo).orElse(null);
        stockTransfer.setCanselReason(canselReason);
        stockTransferRepository.save(stockTransfer);

    }



}











