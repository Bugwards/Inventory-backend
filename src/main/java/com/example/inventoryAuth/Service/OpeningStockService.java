package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.*;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.ItemRepository;
import com.example.inventoryAuth.Repository.LocationRepository;
import com.example.inventoryAuth.Repository.OpeningStockRepository;
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
public class OpeningStockService {

    private final OpeningStockRepository repository;
    private final ItemRepository itemRepository;
    private final StockService stockService;
    private final UserRepository userRepository;
    private final BinCardService binCardService;
    private final LocationRepository locationRepository;

    public OpeningStockService(OpeningStockRepository repository,
            ItemRepository itemRepository,
            StockService stockService,
            UserRepository userRepository,
            BinCardService binCardService,
            LocationRepository locationRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.stockService = stockService;
        this.userRepository = userRepository;
        this.binCardService = binCardService;
        this.locationRepository = locationRepository;
    }

    // =========================
    // CURRENT USER
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
    // CREATE / UPDATE
    // =========================
    @Transactional
    public OpeningStock save(OpeningStockDTO dto, Long id) {
        User currentUser = getCurrentUser();

        OpeningStock os;

        if (id != null) {
            os = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Opening Stock not found"));

            if (os.getStatus() != Status.UNAPPROVED) {
                throw new RuntimeException("Cannot edit processed record");
            }

        } else {
            os = new OpeningStock();
            os.setCreatedBy(currentUser);
            os.setStatus(Status.UNAPPROVED);
            os.setLocation(currentUser.getLocation());
            os.setOpeningDate(dto.getOpeningDate() != null ? dto.getOpeningDate() : LocalDate.now());

            os.setEntryNo("TEMP-" + System.currentTimeMillis());
        }

        // =================
        // VALIDATION
        // =================
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Items required");
        }

        // =================
        // ITEM HANDLING
        // =================
        List<OpeningStockItem> items = os.getItems();

        if (items == null) {
            items = new ArrayList<>();
            os.setItems(items);
        } else {
            items.clear();
        }

        double total = 0;

        for (OpeningStockItemDTO dtoItem : dto.getItems()) {

            Item item = itemRepository.findByItemCode(dtoItem.getItemCode())
                    .orElseThrow(() -> new RuntimeException("Item not found"));

            OpeningStockItem osi = new OpeningStockItem();
            osi.setItem(item);
            osi.setQuantity(dtoItem.getQuantity());
            osi.setUnitPrice(dtoItem.getUnitPrice());
            osi.setOpeningStock(os);

            total += dtoItem.getQuantity() * dtoItem.getUnitPrice();
            items.add(osi);
        }

        os.setTotalValue(total);

        // =========================
        // SAVE
        // =========================

        OpeningStock saved = repository.save(os);

        saved.setEntryNo("OS-" + String.format("%05d", saved.getOpeningStockId()));

        return repository.save(saved);
    }

    // =========================
    // APPROVE
    // =========================
    @Transactional
    public OpeningStock approve(Long id) {
        User currentUser = getCurrentUser();

        OpeningStock os = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opening Stock not found"));

        if (os.getStatus() != Status.UNAPPROVED) {
            throw new RuntimeException("Already processed");
        }

        for (OpeningStockItem item : os.getItems()) {
            Stock stock = stockService.addStock(
                    item.getItem(),
                    os.getLocation(),
                    os.getOpeningStockId(),
                    ReferenceType.OPENING,
                    null,
                    item.getQuantity(),
                    item.getUnitPrice());
            binCardService.createBinCardFromOpeningStock(os, item, stock);
        }

        os.setStatus(Status.APPROVED);
        os.setApprovedBy(currentUser);
        os.setApprovedAt(LocalDateTime.now());

        return repository.save(os);
    }

    // =========================
    // CANCEL
    // =========================
    @Transactional
    public OpeningStock cancel(Long id, String reason) {

        OpeningStock os = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opening Stock not found"));

        if (os.getStatus() != Status.UNAPPROVED) {
            throw new RuntimeException("Cannot cancel processed record");
        }

        os.setStatus(Status.CANCELLED);
        os.setCancelledAt(LocalDateTime.now());

        return repository.save(os);
    }

    // =========================
    // SEARCH
    // =========================
    public List<OpeningStock> search(
            String locationCode,
            Status status,
            String keyword,
            String dateFilter,
            String fromDate,
            String toDate) {

        List<OpeningStock> list = repository.findAll();

        if (locationCode != null && !locationCode.isBlank()) {
            Long locationId = locationRepository.findByCode(locationCode)
                    .map(Location::getId)
                    .orElse(null);
            list = list.stream()
                    .filter(o -> o.getLocation() != null && o.getLocation().getId().equals(locationId))
                    .toList();
        }

        if (status != null) {
            list = list.stream()
                    .filter(o -> o.getStatus() == status)
                    .toList();
        }

        // DATE FILTER
        if (dateFilter != null) {

            LocalDate now = LocalDate.now();

            switch (dateFilter) {

                case "THIS_MONTH":
                    list = list.stream()
                            .filter(o -> o.getOpeningDate().getMonth() == now.getMonth()
                                    && o.getOpeningDate().getYear() == now.getYear())
                            .toList();
                    break;

                case "LAST_MONTH":
                    LocalDate lastMonth = now.minusMonths(1);

                    list = list.stream()
                            .filter(o -> o.getOpeningDate().getMonth() == lastMonth.getMonth()
                                    && o.getOpeningDate().getYear() == lastMonth.getYear())
                            .toList();
                    break;

                case "DATE_RANGE":
                    if (fromDate != null && toDate != null) {

                        LocalDate from = LocalDate.parse(fromDate);
                        LocalDate to = LocalDate.parse(toDate);

                        list = list.stream()
                                .filter(o -> !o.getOpeningDate().isBefore(from)
                                        && !o.getOpeningDate().isAfter(to))
                                .toList();
                    }
                    break;

                default:
                    break;
            }
        }

        // KEYWORD SEARCH
        if (keyword != null && !keyword.isBlank()) {
            list = list.stream()
                    .filter(o -> (o.getEntryNo() != null &&
                            o.getEntryNo().toLowerCase().contains(keyword.toLowerCase())))
                    .toList();
        }

        // SORT DESC
        list = list.stream()
                .sorted((a, b) -> b.getOpeningStockId().compareTo(a.getOpeningStockId()))
                .toList();

        return list;
    }

    public OpeningStockResponseDTO getById(Long id) {

        OpeningStock os = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Opening Stock not found"));

        OpeningStockResponseDTO dto = new OpeningStockResponseDTO();

        dto.setOpeningStockId(os.getOpeningStockId());
        dto.setEntryNo(os.getEntryNo());

        dto.setOpeningDate(os.getOpeningDate());
        dto.setTotalValue(os.getTotalValue());
        dto.setStatus(os.getStatus().name());

        List<OpeningStockItemResponseDTO> itemList = new ArrayList<>();

        for (OpeningStockItem oi : os.getItems()) {

            OpeningStockItemResponseDTO itemDto = new OpeningStockItemResponseDTO();

            itemDto.setOpeningStockItemId(oi.getOpeningStockItemId());
            itemDto.setItemCode(oi.getItem().getItemCode());

            // same GRN style fix
            itemDto.setItemName(
                    oi.getItem().getItemName() != null
                            ? oi.getItem().getItemName()
                            : oi.getItem().getItemCode());

            itemDto.setQuantity(oi.getQuantity());
            itemDto.setUnitPrice(oi.getUnitPrice());

            itemList.add(itemDto);
        }

        dto.setItems(itemList);

        return dto;
    }
}