package com.example.inventoryAuth.Service;


import com.example.inventoryAuth.DTO.ItemResponseSearchByKeyword;
import com.example.inventoryAuth.DTO.StockIssueDto.*;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.*;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class StockIssueService {

    @Autowired
    StockIssueRepository stockIssueRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    GRNItemRepository grnItemRepository;

    @Autowired
    GrnRepository grnRepository;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    UserRepository userRepository;

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


    @Transactional
    public void createStockIssue(StockIssueRequest request){

        StockIssue stockIssue = new StockIssue();
        stockIssue.setDate(request.getDate());
        stockIssue.setDepartment(request.getDepartment());
        stockIssue.setRequestRef(request.getRequestRef());
        stockIssue.setComment(request.getComment());
        stockIssue.setStatus(Status.UNAPPROVED);
        stockIssue.setCurrentLocation(getCurrentUserLocation());

        if(request.getItems()!=null && !request.getItems().isEmpty()){

            for(ItemIssuedDto itemDto : request.getItems()){

                Item item = itemRepository.findByItemCode(itemDto.getItemCode()).orElseThrow(()->new RuntimeException("Item Not Found"));

                if(item == null){
                    throw new RuntimeException("Item not found: " + itemDto.getItemCode());
                }

                for(GrnItemIssuedDto grnDto : itemDto.getGrnItems()){

                    GRN grn = grnRepository.findByGrnNumber(grnDto.getGrnNumber());

                    GRNItem grnItem = grnItemRepository.findByGrnAndItem(grn,item).orElseThrow(()->new RuntimeException("Relevent Records Not Found"));
                    ;

                    Stock stock = stockRepository.findByItemAndGrnItem(item,grnItem);

                    if(stock == null){
                        throw new RuntimeException("Stock not found for item + GRN");
                    }

                    if (grnDto.getIssuedQuantity() > stock.getCurrentQty()) {
                        throw new RuntimeException(
                                "Not enough stock in GRN: " +
                                        stock.getGrnItem().getGrn().getGrnNumber()
                        );
                    }

                    stock.setCurrentQty(stock.getCurrentQty()-grnDto.getIssuedQuantity());
                    stockRepository.save(stock);

                    StockIssueItem issueItem = new StockIssueItem();
                    issueItem.setStockIssue(stockIssue);
                    issueItem.setItem(item);
                    issueItem.setIssuedQuantity(grnDto.getIssuedQuantity());
                    issueItem.setGrnItem(grnItem);

                    stockIssue.getIssueItems().add(issueItem);



                }

            }

            StockIssue savedStockIssue =stockIssueRepository.save(stockIssue);

            String issueNo = "ISU" + String.format("%06d", savedStockIssue.getId());
            savedStockIssue.setIssueNo(issueNo);
            stockIssueRepository.save(savedStockIssue);

        }
    }

    public List<ItemResponseSearchByKeyword> getItemByKeyword(String keyword){
        List<Item> itemList= itemRepository.
                findByItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCaseOrItemDescriptionContainingIgnoreCase(keyword,
                        keyword,
                        keyword);

        List<ItemResponseSearchByKeyword> itemDtoList = new ArrayList<>();
        for(Item item: itemList){
            ItemResponseSearchByKeyword itemDto = new ItemResponseSearchByKeyword();
            itemDto.setItemName(item.getItemName());
            itemDto.setItemCode(item.getItemCode());
            itemDto.setDescription(item.getItemDescription());
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }

    public IssuingItemResponse getItemDetails(String itemCode){
        Item selctedItem = itemRepository.findByItemCode(itemCode).orElseThrow(()->new RuntimeException("Relevent Records Not Found"));
        List<Stock> stockDetails = stockRepository.findByItemId(selctedItem.getId());

        IssuingItemResponse itemDto = new IssuingItemResponse();
        itemDto.setItemCode(itemCode);
        itemDto.setItemGroupName(selctedItem.getItemGroup().getName());
        itemDto.setItemName(selctedItem.getItemName());
        itemDto.setItemDescription(selctedItem.getItemDescription());

        List<GrnWiseStockDto> grnDetailsList = new ArrayList<>();


        for(Stock stock : stockDetails ){
            GrnWiseStockDto grnDetails = new GrnWiseStockDto();
            grnDetails.setCurrentQty(Long.valueOf(stock.getCurrentQty()));
            grnDetails.setGrnNumber(stock.getGrnItem().getGrn().getGrnNumber());
            grnDetails.setGrnDate(stock.getGrnItem().getGrn().getGrnDate());
            grnDetails.setUnitOfMeasurement(stock.getItem().getUnitOfMeasurement());
            grnDetails.setUnitPrice(stock.getUnitPrice());
            grnDetailsList.add(grnDetails);
        }

        itemDto.setGrnStockList(grnDetailsList);

        return  itemDto;

    }

    public List<StockIssueResponse> getAllIssuedRecords(int startPage){
        Page<StockIssue> stockIssueRecords = stockIssueRepository.findAll(PageRequest.of(startPage,5));
        List<StockIssueResponse> responseList = new ArrayList<>();

        for(StockIssue stockIssue: stockIssueRecords.getContent()){
            StockIssueResponse response = new StockIssueResponse();
             response.setIssueNo(stockIssue.getIssueNo());
             response.setIssueDate(stockIssue.getDate());
             response.setLocation(stockIssue.getCurrentLocation());
             response.setDepartment(stockIssue.getDepartment());
             response.setApprovedDate(stockIssue.getApprovedDate());
             response.setStatus(stockIssue.getStatus());
            responseList.add(response);
        }

        return responseList;
    }

    @Transactional
    public void updateStockIssueRecord(StockIssueRequest request, String stockIssueNo){

        StockIssue stockIssue =
                stockIssueRepository.findByIssueNo(stockIssueNo)
                        .orElseThrow(() ->
                                new RuntimeException("No Records Exists"));

        if(stockIssue.getStatus() == Status.APPROVED
                || stockIssue.getStatus() == Status.CANCELLED){

            throw new RuntimeException(
                    "You Can't Update This Issue Record"
            );
        }


        for(StockIssueItem oldIssueItem : stockIssue.getIssueItems()){

            Stock stock = stockRepository.findByItemAndGrnItem(
                    oldIssueItem.getItem(),
                    oldIssueItem.getGrnItem()
            );

            stock.setCurrentQty(
                    stock.getCurrentQty()
                            + oldIssueItem.getIssuedQuantity()
            );

            stockRepository.save(stock);
        }



        stockIssue.getIssueItems().clear();


        stockIssue.setDate(request.getDate());
        stockIssue.setDepartment(request.getDepartment());
        stockIssue.setRequestRef(request.getRequestRef());
        stockIssue.setComment(request.getComment());



        for(ItemIssuedDto itemDto : request.getItems()){

            Item item = itemRepository.findByItemCode(
                    itemDto.getItemCode()
            ).orElseThrow(() ->
                    new RuntimeException("Item not found"));

            for(GrnItemIssuedDto grnDto : itemDto.getGrnItems()){

                GRN grn =
                        grnRepository.findByGrnNumber(
                                grnDto.getGrnNumber()
                        );

                GRNItem grnItem =
                        grnItemRepository.findByGrnAndItem(grn,item)
                                .orElseThrow(() ->
                                        new RuntimeException("GRN Item not found"));

                Stock stock =
                        stockRepository.findByItemAndGrnItem(
                                item,
                                grnItem
                        );

                if(stock == null){
                    throw new RuntimeException("Stock not found");
                }

                if(grnDto.getIssuedQuantity()
                        > stock.getCurrentQty()){

                    throw new RuntimeException(
                            "Not enough stock"
                    );
                }

                // deduct new quantity
                stock.setCurrentQty(
                        stock.getCurrentQty()
                                - grnDto.getIssuedQuantity()
                );

                stockRepository.save(stock);

                StockIssueItem issueItem =
                        new StockIssueItem();

                issueItem.setStockIssue(stockIssue);
                issueItem.setItem(item);
                issueItem.setIssuedQuantity(
                        grnDto.getIssuedQuantity()
                );
                issueItem.setGrnItem(grnItem);

                stockIssue.getIssueItems().add(issueItem);
            }
        }

        stockIssueRepository.save(stockIssue);
    }


    public StockIssueRequest getSelectedIssueRecord(String issueNo){
        StockIssueRequest issueResponse = new StockIssueRequest();
        StockIssue stockIssue = stockIssueRepository.findByIssueNo(issueNo).orElseThrow(()->new RuntimeException("Relevent Records Not Found"));

        if(stockIssue==null){
            throw new RuntimeException("Stock Issue Record not Found");
        }

        issueResponse.setDate(stockIssue.getDate());
        issueResponse.setDepartment(stockIssue.getDepartment());
        issueResponse.setRequestRef(stockIssue.getRequestRef());
        issueResponse.setComment(stockIssue.getComment());

        List<ItemIssuedDto> itemIssuedDtoList = new ArrayList<>();


        for(StockIssueItem stockIssueItem:  stockIssue.getIssueItems()){
            ItemIssuedDto itemIssuedDto = new ItemIssuedDto();
            itemIssuedDto.setItemCode(stockIssueItem.getItem().getItemCode());
            itemIssuedDto.setItemName(stockIssueItem.getItem().getItemName());
            itemIssuedDto.setItemGroupName(stockIssueItem.getItem().getItemGroup().getName());
            itemIssuedDto.setUnitOfMesuremnet(stockIssueItem.getItem().getUnitOfMeasurement());
            itemIssuedDto.setDescription(stockIssueItem.getItem().getItemDescription());

            List<GrnItemIssuedDto> grnItemIssuedDtoList = new ArrayList<>();

            GRNItem grnItem = stockIssueItem.getGrnItem();

            GrnItemIssuedDto grnItemIssuedDto = new GrnItemIssuedDto();

            Stock stockRecord =
                    stockRepository.findByItemAndGrnItem(
                            stockIssueItem.getItem(),
                            grnItem
                    );

            grnItemIssuedDto.setGrnNumber(
                    grnItem.getGrn().getGrnNumber()
            );

            grnItemIssuedDto.setGrnDate(
                    grnItem.getGrn().getGrnDate()
            );

            grnItemIssuedDto.setCurrentQuantity(
                    Long.valueOf(stockRecord.getCurrentQty())
            );

            grnItemIssuedDto.setIssuedQuantity(
                    stockIssueItem.getIssuedQuantity()
            );

            grnItemIssuedDtoList.add(grnItemIssuedDto);


             itemIssuedDto.setGrnItems(grnItemIssuedDtoList);
            itemIssuedDtoList.add(itemIssuedDto);
        }

        issueResponse.setItems(itemIssuedDtoList);

        return issueResponse;

    }

    public void approveRecord(String issueNo){
       StockIssue stockIssue = stockIssueRepository.findByIssueNo(issueNo).orElseThrow(()->new RuntimeException("Relevent Records Not Found"));

        if(stockIssue.getStatus() == Status.APPROVED){
            throw new RuntimeException("Already Approved");
        }

        if(stockIssue.getStatus() == Status.CANCELLED){
            throw new RuntimeException("Can't Be Approved");
        }

       stockIssue.setStatus(Status.APPROVED);

       List<StockIssueItem> stockIssueItemList = stockIssue.getIssueItems();
       for(StockIssueItem stockIssueItem : stockIssueItemList){

           Stock stock = stockRepository.findByItemAndGrnItem(stockIssueItem.getItem(),stockIssueItem.getGrnItem());

           stock.setActualQty(stock.getActualQty() - stockIssueItem.getIssuedQuantity());
           stockRepository.save(stock);
       }

       stockIssueRepository.save(stockIssue);

    }

    public void cancelRecord(String issueNo){
        StockIssue stockIssue = stockIssueRepository.findByIssueNo(issueNo).orElseThrow(()->new RuntimeException("Relevent Records Not Found"));

        if(stockIssue.getStatus() == Status.CANCELLED){
            throw new RuntimeException("Already Cancelled");
        }

        if(stockIssue.getStatus() == Status.APPROVED){
            throw new RuntimeException("Can't Be Cancelled");
        }

        stockIssue.setStatus(Status.CANCELLED);

        List<StockIssueItem> stockIssueItemList = stockIssue.getIssueItems();
        for(StockIssueItem stockIssueItem : stockIssueItemList){

            Stock stock = stockRepository.findByItemAndGrnItem(stockIssueItem.getItem(),stockIssueItem.getGrnItem());

            stock.setCurrentQty(stock.getCurrentQty() + stockIssueItem.getIssuedQuantity());
            stockRepository.save(stock);
        }

        stockIssueRepository.save(stockIssue);

    }

    public void saveCancelReason(String cancelMsg,String issueNo){

        StockIssue stockIssue = stockIssueRepository.findByIssueNo(issueNo).orElseThrow(()->new RuntimeException("Relevent Records Not Found"));

        stockIssue.setCancelMsg(cancelMsg);

        stockIssueRepository.save(stockIssue);

    }

    public List<StockIssueResponse> getFilteredIssues(StockIssueFilterRequest request) {

        Specification<StockIssue> spec = buildFilter(request);

        Sort sort = Sort.by(
                request.getSortDirection().equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC,
                request.getSortBy().equals("ISSUE_DATE") ? "date" : "issueNo"
        );

        List<StockIssue> stockIssueList = stockIssueRepository.findAll(spec, sort);
        List<StockIssueResponse> issueResponseList = new ArrayList<>();

        for(StockIssue stockIssue : stockIssueList){
            StockIssueResponse response = new StockIssueResponse();
            response.setIssueDate(stockIssue.getDate());
            response.setIssueNo(stockIssue.getIssueNo());
            response.setDepartment(stockIssue.getDepartment());
            response.setStatus(stockIssue.getStatus());
            response.setLocation(stockIssue.getCurrentLocation());
            response.setApprovedDate(stockIssue.getApprovedDate());
            issueResponseList.add(response);
        }

        return issueResponseList;
    }



    public Specification<StockIssue> buildFilter(StockIssueFilterRequest req) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();


            if (req.getStatus() != null && !req.getStatus().equals("ALL")) {
                predicates.add(cb.equal(root.get("status"), req.getStatus()));
            }

            if (req.getLocation() != null && !req.getLocation().equals("ALL")) {
                predicates.add(cb.equal(root.get("currentLocation"), req.getLocation()));
            }

            if (req.getDepartment() != null && !req.getDepartment().equals("ALL")) {
                predicates.add(cb.equal(root.get("department"), req.getDepartment()));
            }

            if (req.getSearch() != null && !req.getSearch().isEmpty()) {
                predicates.add(
                        cb.like(root.get("issueNo"), "%" + req.getSearch() + "%")
                );
            }

            if (req.getIssueDateFilter() != null) {
                LocalDate now = LocalDate.now();

                switch (req.getIssueDateFilter()) {

                    case "THIS_MONTH":
                        predicates.add(cb.between(
                                root.get("date"),
                                now.withDayOfMonth(1),
                                now
                        ));
                        break;

                    case "LAST_AND_THIS_MONTH":
                        predicates.add(cb.between(
                                root.get("date"),
                                now.minusMonths(1).withDayOfMonth(1),
                                now
                        ));
                        break;

                    case "DATE_RANGE":
                        if (req.getFromDate() != null && req.getToDate() != null) {
                            predicates.add(cb.between(
                                    root.get("date"),
                                    req.getFromDate(),
                                    req.getToDate()
                            ));
                        }
                        break;

                    case "ALL":
                    default:
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }



}
