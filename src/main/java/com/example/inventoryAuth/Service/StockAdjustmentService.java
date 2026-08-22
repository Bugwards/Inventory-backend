package com.example.inventoryAuth.Service;


import com.example.inventoryAuth.DTO.StockAdjustmentDTO;
import com.example.inventoryAuth.DTO.StockAdjustmentItemDTO;
import com.example.inventoryAuth.DTO.StockAdjustmentItemResponseDTO;
import com.example.inventoryAuth.DTO.StockAdjustmentResponseDTO;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.StockAdjustmentRepository;
import com.example.inventoryAuth.Repository.StockRepository;
import com.example.inventoryAuth.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockAdjustmentService {

    private final StockAdjustmentRepository repository;
    private final StockRepository stockRepository;
    private final StockService stockService;
    private final UserRepository userRepository;
    private final BinCardService binCardService;

    public StockAdjustmentService(StockAdjustmentRepository repository,
                                  StockRepository stockRepository,
                                  StockService stockService,
                                  UserRepository userRepository,
                                  BinCardService binCardService
    ) {
        this.repository = repository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
        this.userRepository = userRepository;
        this.binCardService = binCardService;
    }


    // =========================
    //  CURRENT USER
    // =========================
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    // =========================
    // SAVE + AUTO APPROVE (DTO VERSION)
    // =========================
    @Transactional
    public StockAdjustment save(StockAdjustmentDTO dto) {

        User currentUser = getCurrentUser();
        StockAdjustment adjustment = new StockAdjustment();

        User user = null;



        adjustment.setStatus(Status.APPROVED);
        adjustment.setCreatedBy(currentUser);
        adjustment.setApprovedBy(currentUser);
        adjustment.setApprovedAt(java.time.LocalDateTime.now());

        adjustment.setAdjustmentDate(
                dto.getAdjustmentDate() != null ? dto.getAdjustmentDate() : LocalDate.now()
        );

        adjustment.setLocation(currentUser.getLocation());

        adjustment.setReason(dto.getReason());
        adjustment.setComment(dto.getComment());

        adjustment.setAdjustmentNo("TEMP-" + System.currentTimeMillis());

        // =========================
        // ITEM MAPPING
        // =========================
        List<StockAdjustmentItem> itemList = new ArrayList<>();

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Items required");
        }

        for (StockAdjustmentItemDTO i : dto.getItems()) {

            StockAdjustmentItem item = new StockAdjustmentItem();

            Stock stock = stockRepository.findById(i.getStockId())
                    .orElseThrow(() -> new RuntimeException("Stock not found"));

            if (i.getQuantity() == null || i.getQuantity() == 0) {
                throw new RuntimeException("Invalid quantity");
            }

            item.setStock(stock);
            item.setQuantity(i.getQuantity());
            item.setAdjustment(adjustment);

            itemList.add(item);
        }

        adjustment.setItems(itemList);

        // =========================
        // SAVE FIRST
        // =========================
        StockAdjustment saved = repository.save(adjustment);

        // FINAL NUMBER
        saved.setAdjustmentNo("ADJ-" + String.format("%05d", saved.getAdjustmentId()));
        saved = repository.save(saved);

        // =========================
        // APPLY STOCK UPDATE
        // =========================
        for (StockAdjustmentItem item : saved.getItems()) {

            Stock stock = stockRepository.findById(item.getStock().getStockId())
                    .orElseThrow(() -> new RuntimeException("Stock not found"));

            // NEGATIVE VALIDATION
            if (item.getQuantity() < 0 &&
                    stock.getCurrentQty() + item.getQuantity() < 0) {

                throw new RuntimeException(
                        "Not enough stock for item: " + stock.getItem().getItemName()
                );
            }

            stockService.adjustStockDirect(stock, item.getQuantity());

            binCardService.createBinCardFromStockAdjustment(
                    saved,
                    item,
                    stock
            );
        }

        return saved;
    }

    // =========================
    // SEARCH (UNCHANGED)
    // =========================
    public List<StockAdjustment> search(
            Location location,
            String keyword,
            String dateFilter,
            String fromDate,
            String toDate
    ) {

        List<StockAdjustment> list = repository.findAll();

        if (location != null) {
            list = list.stream()
                    .filter(a -> a.getLocation() == location)
                    .toList();
        }

        if (dateFilter != null) {

            LocalDate now = LocalDate.now();

            switch (dateFilter) {

                case "THIS_MONTH":
                    list = list.stream()
                            .filter(a -> a.getAdjustmentDate().getMonth() == now.getMonth()
                                    && a.getAdjustmentDate().getYear() == now.getYear())
                            .toList();
                    break;

                case "LAST_MONTH":
                    LocalDate lastMonth = now.minusMonths(1);

                    list = list.stream()
                            .filter(a -> a.getAdjustmentDate().getMonth() == lastMonth.getMonth()
                                    && a.getAdjustmentDate().getYear() == lastMonth.getYear())
                            .toList();
                    break;

                case "DATE_RANGE":
                    if (fromDate != null && toDate != null) {

                        LocalDate from = LocalDate.parse(fromDate);
                        LocalDate to = LocalDate.parse(toDate);

                        list = list.stream()
                                .filter(a -> !a.getAdjustmentDate().isBefore(from)
                                        && !a.getAdjustmentDate().isAfter(to))
                                .toList();
                    }
                    break;
            }
        }

        if (keyword != null && !keyword.isBlank()) {
            list = list.stream()
                    .filter(a ->
                            a.getAdjustmentNo() != null &&
                                    a.getAdjustmentNo().toLowerCase().contains(keyword.toLowerCase())
                    )
                    .toList();
        }

        return list.stream()
                .sorted((a, b) -> b.getAdjustmentId().compareTo(a.getAdjustmentId()))
                .toList();
    }

    // =========================
    // VIEW
    // =========================
    public StockAdjustmentResponseDTO getById(Long id) {

        StockAdjustment adjustment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock Adjustment not found"));

        StockAdjustmentResponseDTO dto = new StockAdjustmentResponseDTO();

        dto.setAdjustmentId(adjustment.getAdjustmentId());
        dto.setAdjustmentNo(adjustment.getAdjustmentNo());
        dto.setAdjustmentDate(adjustment.getAdjustmentDate());
        dto.setReason(adjustment.getReason());
        dto.setComment(adjustment.getComment());
        dto.setLocation(adjustment.getLocation());
        dto.setStatus(adjustment.getStatus().name());

        dto.setCreatedBy(
                adjustment.getCreatedBy() != null
                        ? adjustment.getCreatedBy().getUsername()
                        : null
        );

        dto.setApprovedBy(
                adjustment.getApprovedBy() != null
                        ? adjustment.getApprovedBy().getUsername()
                        : null
        );

        // =========================
        // ITEMS
        // =========================
        List<StockAdjustmentItemResponseDTO> itemList = new ArrayList<>();

        for (StockAdjustmentItem i : adjustment.getItems()) {

            StockAdjustmentItemResponseDTO itemDto = new StockAdjustmentItemResponseDTO();

            itemDto.setAdjustmentItemId(i.getAdjustmentItemId());

            itemDto.setStockId(i.getStock().getStockId());
            itemDto.setItemCode(i.getStock().getItem().getItemCode());
            itemDto.setItemName(i.getStock().getItem().getItemName());

            itemDto.setGrnId(i.getGrn() != null ? i.getGrn().getGrnId() : null);
            itemDto.setQuantity(i.getQuantity());

            itemList.add(itemDto);
        }

        dto.setItems(itemList);

        return dto;
    }
}