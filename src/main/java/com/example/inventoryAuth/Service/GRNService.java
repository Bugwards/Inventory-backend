package com.example.inventoryAuth.Service;


import com.example.inventoryAuth.DTO.GRNDTO;
import com.example.inventoryAuth.DTO.GRNItemDTO;
import com.example.inventoryAuth.DTO.GRNItemResponseDTO;
import com.example.inventoryAuth.DTO.GRNResponseDTO;

import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.GrnRepository;
import com.example.inventoryAuth.Repository.ItemRepository;
import com.example.inventoryAuth.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GRNService {

    private final GrnRepository grnRepository;
    private final ItemRepository itemRepository;
    private final StockService stockService;
    private final UserRepository userRepository;


    public GRNService(GrnRepository grnRepository,
                      ItemRepository itemRepository,
                      StockService stockService,
                      UserRepository userRepository
                      ) {
        this.grnRepository = grnRepository;
        this.itemRepository = itemRepository;
        this.stockService = stockService;
        this.userRepository = userRepository;

    }

    // =========================
    // 🔐 CURRENT USER
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
    //  CREATE / UPDATE
    // =========================
    @Transactional
    public GRN save(GRNDTO dto, Long id) {
        User currentUser = getCurrentUser();

        GRN grn;

        if (id != null) {
            grn = grnRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("GRN not found"));

            if (grn.getStatus() != Status.UNAPPROVED) {
                throw new RuntimeException("Cannot edit processed GRN");
            }

        } else {
            grn = new GRN();
            grn.setStatus(Status.UNAPPROVED);
            grn.setCreatedBy(currentUser);
            grn.setLocation(currentUser.getLocation());
            grn.setItems(new ArrayList<>());
            grn.setGrnDate(dto.getGrnDate() != null ? dto.getGrnDate() : LocalDate.now());


            grn.setGrnNumber("TEMP-" + System.currentTimeMillis());
        }

        // =========================
        // VALIDATION
        // =========================
        if (dto.getPoNumber() == null || dto.getPoNumber().isBlank()) {
            throw new RuntimeException("PO Number required");
        }

        if (dto.getSupplier() == null) {
            throw new RuntimeException("Supplier required");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Items required");
        }



        grn.setPoNumber(dto.getPoNumber());
        grn.setSupplier(dto.getSupplier());

        // =================
        // ITEM HANDLING
        // =================
        List<GRNItem> items = grn.getItems();

        if (items == null) {
            items = new ArrayList<>();
            grn.setItems(items);
        } else {
            items.clear();
        }

        double total = 0;

        for (GRNItemDTO dtoItem : dto.getItems()) {

            Item item = itemRepository.findByItemCode(dtoItem.getItemCode())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            GRNItem gi = new GRNItem();
            gi.setItem(item);
            gi.setActualQty(dtoItem.getQuantity());
            gi.setCurrentQty(dtoItem.getQuantity());
            gi.setQuantity(dtoItem.getQuantity());
            gi.setUnitPrice(dtoItem.getUnitPrice());
            gi.setGrn(grn);

            total += dtoItem.getQuantity() * dtoItem.getUnitPrice();
            items.add(gi);
        }

        grn.setTotalValue(total);

        // =========================
        // SAVE
        // =========================

        GRN saved = grnRepository.save(grn);

        // FINAL NUMBER
        saved.setGrnNumber("GRN-" + String.format("%05d", saved.getGrnId()));

        return grnRepository.save(saved);
    }


    // =========================
    //  APPROVE
    // =========================
    @Transactional
    public GRN approve(Long id) {
        User currentUser = getCurrentUser();

        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GRN not found"));

        if (grn.getStatus() != Status.UNAPPROVED) {
            throw new RuntimeException("Already processed");
        }

        for (GRNItem item : grn.getItems()) {
            stockService.addStock(
                    item.getItem(),
                    grn.getLocation(),
                    grn.getGrnId(),
                    ReferenceType.GRN,
                    item.getQuantity(),
                    item.getUnitPrice()
            );
        }

        grn.setStatus(Status.APPROVED);
        grn.setApprovedBy(currentUser);
        grn.setApprovedAt(LocalDateTime.now());

        return grnRepository.save(grn);
    }

    // =========================
    //  CANCEL
    // =========================
    @Transactional
    public GRN cancel(Long id, String reason) {

        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GRN not found"));

        if (grn.getStatus() != Status.UNAPPROVED) {
            throw new RuntimeException("Cannot cancel processed GRN");
        }

        grn.setStatus(Status.CANCELLED);
        grn.setCancelledAt(LocalDateTime.now());
        grn.setCancelReason(reason);

        return grnRepository.save(grn);
    }

    // =========================
    // SEARCH
    // =========================
    public List<GRN> search(
            Location location,
            Status status,
            String keyword,
            String dateFilter,
            String fromDate,
            String toDate
    ) {

        List<GRN> list = grnRepository.findAll();

        // FILTER BY STATUS
        if (status != null) {
            list = list.stream()
                    .filter(g -> g.getStatus() == status)
                    .toList();
        }

        if (location != null) {
            list = list.stream()
                    .filter(g -> g.getLocation() != null && g.getLocation().equals(location))
                    .toList();
        }


        // FILTER BY DATE
        if (dateFilter != null) {

            LocalDate now = LocalDate.now();

            switch (dateFilter) {

                case "THIS_MONTH":
                    list = list.stream()
                            .filter(g -> g.getGrnDate().getMonth() == now.getMonth()
                                    && g.getGrnDate().getYear() == now.getYear())
                            .toList();
                    break;

                case "LAST_MONTH":
                    LocalDate lastMonth = now.minusMonths(1);

                    list = list.stream()
                            .filter(g -> g.getGrnDate().getMonth() == lastMonth.getMonth()
                                    && g.getGrnDate().getYear() == lastMonth.getYear())
                            .toList();
                    break;

                case "DATE_RANGE":
                    if (fromDate != null && toDate != null) {

                        LocalDate from = LocalDate.parse(fromDate);
                        LocalDate to = LocalDate.parse(toDate);

                        list = list.stream()
                                .filter(g -> !g.getGrnDate().isBefore(from)
                                        && !g.getGrnDate().isAfter(to))
                                .toList();
                    }
                    break;
            }
        }

        // SEARCH (GRN NO + SUPPLIER)
        if (keyword != null && !keyword.isBlank()) {
            list = list.stream()
                    .filter(g ->
                            (g.getGrnNumber() != null &&
                                    g.getGrnNumber().toLowerCase().contains(keyword.toLowerCase()))
                                    ||
                                    (g.getSupplier() != null &&
                                            g.getSupplier().getDisplayName().toLowerCase().contains(keyword.toLowerCase()))
                    )
                    .toList();
        }

        // DEFAULT SORT (DESC)
        list = list.stream()
                .sorted((a, b) -> b.getGrnId().compareTo(a.getGrnId()))
                .toList();

        return list;
    }

    public GRNResponseDTO getById(Long id) {

        GRN grn = grnRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GRN not found"));

        GRNResponseDTO dto = new GRNResponseDTO();

        dto.setGrnId(grn.getGrnId());
        dto.setGrnNumber(grn.getGrnNumber());
        dto.setPoNumber(grn.getPoNumber());
        dto.setLocation(grn.getLocation() != null
                ? Location.valueOf(grn.getLocation().name())
                : null);


        dto.setSupplier(grn.getSupplier() != null
                ? grn.getSupplier().name()
                : null);

        dto.setGrnDate(grn.getGrnDate());
        dto.setTotalValue(grn.getTotalValue());
        dto.setStatus(grn.getStatus().name());

        dto.setCreatedBy(grn.getCreatedBy() != null
                ? grn.getCreatedBy().getUsername()
                : null);

        dto.setApprovedBy(grn.getApprovedBy() != null
                ? grn.getApprovedBy().getUsername()
                : null);
        dto.setComment(grn.getComment());

        List<GRNItemResponseDTO> itemList = new ArrayList<>();

        for (GRNItem gi : grn.getItems()) {

            GRNItemResponseDTO itemDto = new GRNItemResponseDTO();

            itemDto.setGrnItemId(gi.getGrnItemId());
            itemDto.setItemCode(gi.getItem().getItemCode());


            itemDto.setItemName(
                    gi.getItem().getItemName() != null
                            ? gi.getItem().getItemName()
                            : gi.getItem().getItemCode()
            );

            itemDto.setQuantity(gi.getQuantity());
            itemDto.setUnitPrice(gi.getUnitPrice());

            itemList.add(itemDto);
        }

        dto.setItems(itemList);

        return dto;
    }}
