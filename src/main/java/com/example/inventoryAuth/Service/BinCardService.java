package com.example.inventoryAuth.Service;


import com.example.inventoryAuth.DTO.BinCardDTO;
import com.example.inventoryAuth.DTO.BinCardItemDTO;
import com.example.inventoryAuth.DTO.BinCardSummaryDTO;
import com.example.inventoryAuth.Entity.*;
import com.example.inventoryAuth.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.inventoryAuth.DTO.DashboardAnalyticsDTO;
import com.example.inventoryAuth.DTO.StockLevelDTO;
import com.example.inventoryAuth.DTO.MovementTrendDTO;
import com.example.inventoryAuth.DTO.RecentTransactionDTO;
import com.example.inventoryAuth.DTO.MovementSummaryDTO;

import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

import java.time.LocalDate;
import java.util.List;

@Service
public class BinCardService {

    @Autowired
    private BinCardRepository binCardRepo;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    GRNItemRepository grnItemRepository;

    @Autowired
    private StockIssueItemRepository stockIssueItemRepo;

    @Autowired
    private StockTransferItemRepository stockTransferItemRepo;

    @Autowired
    private OpeningStockItemRepository openingStockItemRepo;

    @Autowired
    private StockAdjustmentItemRepository stockAdjustmentItemRepo;

    @Autowired
    private StockRepository stockRepo;

    // ---------------- SEARCH ITEMS ----------------
    public List<BinCardItemDTO> searchItems(String itemName) {
        return itemRepo.findByItemNameContainingIgnoreCase(itemName)
                .stream()
                .map(item -> {
                    BinCardItemDTO dto = new BinCardItemDTO();
                    dto.setItemCode(item.getItemCode());
                    dto.setItemName(item.getItemName());
                    return dto;
                })
                .toList();
    }

    // ---------------- GET BIN CARD HISTORY ----------------
    public List<BinCardDTO> getBinCardByItemCode(String itemCode) {

        Item item = itemRepo.findByItemCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not Found"));

        List<BinCard> records = binCardRepo
                .findByItemOrderByDateAsc(item);

        return records.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private Long getTotalCurrentBalance(Item item) {
        return stockRepo.findByItem(item)
                .stream()
                .mapToLong(stock -> stock.getCurrentQty().longValue())
                .sum();
    }

    // create Bincard from GRN
    public void createBinCardFromGRN(GRN grn, GRNItem grnItem, Stock stock) {

        BinCard binCard = new BinCard();
        binCard.setItem(grnItem.getItem());
        binCard.setDate(grn.getGrnDate());
        binCard.setType(TransactionType.INWARD);
        binCard.setReference(grn.getGrnNumber());
        binCard.setInwardQty(grnItem.getQuantity().longValue());
        binCard.setOutwardQty(0L);
        binCard.setBalance(getTotalCurrentBalance(stock.getItem()));
        binCard.setRemarks("Received to Head Office");
        binCardRepo.save(binCard);
    }

    // create Bincard from StockIssue
    public void createBinCardFromStockIssue(Stock stock, StockIssue stockIssue, StockIssueItem stockIssueItem) {
        BinCard binCard = new BinCard();
        binCard.setItem(stockIssueItem.getItem());
        binCard.setDate(stockIssue.getDate());
        binCard.setType(TransactionType.OUTWARD);
        binCard.setReference(stockIssue.getIssueNo());
        binCard.setInwardQty(0L);
        binCard.setOutwardQty(stockIssueItem.getIssuedQuantity().longValue());
        binCard.setItem(stockIssueItem.getItem());
        binCard.setBalance(getTotalCurrentBalance(stock.getItem()));
        binCard.setRemarks(stockIssue.getComment());

        binCardRepo.save(binCard);
    }

    //create binCard from stockTransfer
    public void createBinCardFromStockTransfer(
            Stock stock,
            StockTransfer stockTransfer,
            StockTransferItem stockTransferItem
    ) {
        BinCard binCard = new BinCard();

        binCard.setItem(stockTransferItem.getItem());
        binCard.setDate(stockTransfer.getTransferDate());
        binCard.setType(TransactionType.OUTWARD);
        binCard.setReference(stockTransfer.getTransferNo());

        binCard.setInwardQty(0L);
        binCard.setOutwardQty(stockTransferItem.getTransferQty().longValue());

        // stock currentQty is already reduced before calling this method
        binCard.setBalance(getTotalCurrentBalance(stock.getItem()));

        binCard.setRemarks(
                stockTransfer.getComment() != null
                        ? stockTransfer.getComment()
                        : "Stock Transfer"
        );

        binCardRepo.save(binCard);
    }

    // create Bincard from OopeningStock
    public void createBinCardFromOpeningStock(
            OpeningStock openingStock,
            OpeningStockItem openingStockItem,
            Stock stock
    ) {
        BinCard binCard = new BinCard();

        binCard.setItem(openingStockItem.getItem());
        binCard.setDate(openingStock.getOpeningDate());
        binCard.setType(TransactionType.INWARD);
        binCard.setReference(openingStock.getEntryNo());
        binCard.setInwardQty(openingStockItem.getQuantity().longValue());
        binCard.setOutwardQty(0L);
        binCard.setBalance(getTotalCurrentBalance(stock.getItem()));
        binCard.setRemarks("Opening Stock");

        binCardRepo.save(binCard);
    }

    //stock adjustment
    public void createBinCardFromStockAdjustment(
            StockAdjustment adjustment,
            StockAdjustmentItem adjustmentItem,
            Stock stock
    ) {
        BinCard binCard = new BinCard();

        int qty = adjustmentItem.getQuantity();

        binCard.setItem(stock.getItem());
        binCard.setDate(adjustment.getAdjustmentDate());
        binCard.setReference(adjustment.getAdjustmentNo());

        if (qty > 0) {
            binCard.setType(TransactionType.INWARD);
            binCard.setInwardQty((long) qty);
            binCard.setOutwardQty(0L);
            binCard.setRemarks("Stock Adjustment (+)");
        } else {
            binCard.setType(TransactionType.OUTWARD);
            binCard.setInwardQty(0L);
            binCard.setOutwardQty((long) Math.abs(qty));
            binCard.setRemarks("Stock Adjustment (-)");
        }

        binCard.setBalance(getTotalCurrentBalance(stock.getItem()));

        binCardRepo.save(binCard);
    }

    private BinCardDTO mapToDTO(BinCard b) {

        BinCardDTO dto = new BinCardDTO();

        dto.setDate(b.getDate());
        dto.setType(b.getType());
        dto.setReference(b.getReference());
        dto.setInwardQty(b.getInwardQty() == null ? 0L : b.getInwardQty());
        dto.setOutwardQty(b.getOutwardQty() == null ? 0L : b.getOutwardQty());
        dto.setBalance(b.getBalance());
        dto.setRemarks(b.getRemarks());
        dto.setItemCode(b.getItem().getItemCode());
        dto.setItemName(b.getItem().getItemName());

        return dto;
    }

    public BinCardSummaryDTO calculateTotalInwardAndOutward(String itemCode) {
        Item item = itemRepo.findByItemCode(itemCode).orElse(null);

        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        Long totalInward = 0L;

        List<OpeningStockItem> openingStockList =
                openingStockItemRepo.findByItemAndOpeningStock_OpeningDateAfter(item, thirtyDaysAgo);

        Long totalOpening = 0L;

        for (OpeningStockItem os : openingStockList) {
            totalOpening += os.getQuantity().longValue();
        }

        Long totalGrn = 0L;
        List<GRNItem> grnItemList = grnItemRepository.findByItemAndGrn_GrnDateAfter(item, thirtyDaysAgo);

        for (GRNItem grnItem : grnItemList) {
            totalGrn += grnItem.getQuantity();
        }

        List<StockAdjustmentItem> adjustmentList =
                stockAdjustmentItemRepo
                        .findByStock_ItemAndAdjustment_AdjustmentDateAfter(item, thirtyDaysAgo);

        Long adjustmentInward = 0L;
        Long adjustmentOutward = 0L;

        for (StockAdjustmentItem adjItem : adjustmentList) {

            if (adjItem.getQuantity() == null) continue;

            long qty = adjItem.getQuantity();

            if (qty > 0) {
                adjustmentInward += qty;
            } else if (qty < 0) {
                adjustmentOutward += Math.abs(qty);
            }
        }

        totalInward = totalGrn + totalOpening + adjustmentInward;



        List<StockIssueItem> stockIssueItemList = stockIssueItemRepo.findByItemAndStockIssue_DateAfter(item, thirtyDaysAgo);
        Long totalIssue = 0L;

        for (StockIssueItem stockIssueItem : stockIssueItemList) {
            totalIssue += stockIssueItem.getIssuedQuantity();
        }

        List<StockTransferItem> stockTransferItemList = stockTransferItemRepo.findByItemAndStockTransfer_TransferDateAfter(item, thirtyDaysAgo);

        Long totalTransfer = 0L;

        for (StockTransferItem stockTransferItem : stockTransferItemList) {
            totalTransfer += stockTransferItem.getTransferQty();
        }
        Long totalOutward = adjustmentOutward+totalIssue+totalTransfer;
        Double averageMonthlyUsage =  (totalOutward+ totalInward)/2.0;

        MovementType movementType;

        if (averageMonthlyUsage > 50) {
            movementType = MovementType.FAST;
        } else {
            movementType = MovementType.SLOW;
        }
        List<BinCard> records = binCardRepo.findByItemOrderByDateAsc(item);
        List<BinCardDTO> history = records.stream()
                .map(this::mapToDTO)
                .toList();


        BinCardSummaryDTO summaryDTO = new BinCardSummaryDTO();
        summaryDTO.setItemCode(item.getItemCode());
        summaryDTO.setItemName(item.getItemName());
        summaryDTO.setMinimumLevel(item.getMinimumLevel());
        summaryDTO.setReorderLevel(item.getReorderQuantity());
        summaryDTO.setTotalInward(totalInward);
        summaryDTO.setTotalOutward(totalOutward);
        summaryDTO.setAverageMonthlyUsage(averageMonthlyUsage);
        summaryDTO.setMovementType(movementType);
        summaryDTO.setHistory(history);

        return summaryDTO;
    }

    public DashboardAnalyticsDTO getDashboardAnalytics() {

        DashboardAnalyticsDTO dashboard = new DashboardAnalyticsDTO();

        // =========================================================
        // 1. GET ALL ITEMS
        // =========================================================

        List<Item> items = itemRepo.findAll();

        dashboard.setTotalItems(items.size());

        // =========================================================
        // 2. CURRENT STOCK + STOCK LEVEL ANALYSIS
        // =========================================================

        List<StockLevelDTO> stockLevels = new ArrayList<>();

        long totalStock = 0L;
        long lowStockItems = 0L;
        long fastMovingItems = 0L;
        long slowMovingItems = 0L;

        for (Item item : items) {

            long currentStock = getTotalCurrentBalance(item);

            long minimumLevel =
                    item.getMinimumLevel() != null
                            ? item.getMinimumLevel().longValue()
                            : 0L;

            long reorderLevel =
                    item.getReorderQuantity() != null
                            ? item.getReorderQuantity().longValue()
                            : 0L;

            // -----------------------------------------------------
            // Determine stock status
            // -----------------------------------------------------

            String status;

            if (currentStock <= minimumLevel) {

                status = "LOW";
                lowStockItems++;

            } else if (currentStock <= reorderLevel) {

                status = "REORDER";

            } else {

                status = "HEALTHY";
            }

            totalStock += currentStock;

            // -----------------------------------------------------
            // Determine movement type
            // -----------------------------------------------------

            try {

                BinCardSummaryDTO summary =
                        calculateTotalInwardAndOutward(
                                item.getItemCode()
                        );

                if (summary.getMovementType() == MovementType.FAST) {

                    fastMovingItems++;

                } else {

                    slowMovingItems++;
                }

            } catch (Exception e) {

                System.out.println(
                        "Unable to calculate movement for item: "
                                + item.getItemCode()
                                + " - "
                                + e.getMessage()
                );

                slowMovingItems++;
            }

            // -----------------------------------------------------
            // IMPORTANT:
            // Actually put values into StockLevelDTO
            // -----------------------------------------------------

            StockLevelDTO stockLevel =
                    new StockLevelDTO(
                            item.getItemCode(),
                            item.getItemName(),
                            currentStock,
                            minimumLevel,
                            reorderLevel,
                            status
                    );
            if ("LOW".equals(status)) {
                stockLevels.add(stockLevel);
            }
            stockLevels.add(stockLevel);
        }

        dashboard.setTotalStock(totalStock);
        dashboard.setLowStockItems(lowStockItems);
        dashboard.setFastMovingItems(fastMovingItems);
        dashboard.setSlowMovingItems(slowMovingItems);

        // =========================================================
        // 3. SORT STOCK LEVELS
        // LOWEST STOCK FIRST
        // =========================================================

        stockLevels = stockLevels.stream()
                .filter(stock -> "LOW".equals(stock.getStatus()))
                .sorted(
                        Comparator.comparingLong(
                                StockLevelDTO::getCurrentStock
                        )
                )
                .limit(10)
                .toList();

        dashboard.setStockLevels(stockLevels);

        // =========================================================
        // 4. GET ALL BIN CARD TRANSACTIONS
        // =========================================================

        List<BinCard> allTransactions =
                binCardRepo.findAll();

        // =========================================================
        // 5. TOTAL INWARD
        // =========================================================

        long totalInward =
                allTransactions.stream()
                        .mapToLong(
                                b -> b.getInwardQty() == null
                                        ? 0L
                                        : b.getInwardQty()
                        )
                        .sum();

        // =========================================================
        // 6. TOTAL OUTWARD
        // =========================================================

        long totalOutward =
                allTransactions.stream()
                        .mapToLong(
                                b -> b.getOutwardQty() == null
                                        ? 0L
                                        : b.getOutwardQty()
                        )
                        .sum();

        dashboard.setTotalInward(totalInward);
        dashboard.setTotalOutward(totalOutward);

        // =========================================================
        // 7. INVENTORY HEALTH
        // =========================================================

        if (!items.isEmpty()) {

            long healthyItems =
                    items.size() - lowStockItems;

            double healthPercentage =
                    ((double) healthyItems / items.size()) * 100.0;

            healthPercentage =
                    Math.round(healthPercentage * 10.0) / 10.0;

            dashboard.setStockHealthPercentage(
                    healthPercentage
            );

        } else {

            dashboard.setStockHealthPercentage(0.0);
        }

        // =========================================================
        // 8. STOCK MOVEMENT TREND
        // LAST 6 MONTHS
        // =========================================================

        LocalDate today = LocalDate.now();

        List<MovementTrendDTO> movementTrend =
                new ArrayList<>();

        for (int i = 5; i >= 0; i--) {

            YearMonth month =
                    YearMonth.from(
                            today.minusMonths(i)
                    );

            long inward =
                    allTransactions.stream()

                            .filter(b ->
                                    b.getDate() != null
                                            &&
                                            YearMonth.from(b.getDate())
                                                    .equals(month)
                            )

                            .mapToLong(
                                    b -> b.getInwardQty() == null
                                            ? 0L
                                            : b.getInwardQty()
                            )

                            .sum();

            long outward =
                    allTransactions.stream()

                            .filter(b ->
                                    b.getDate() != null
                                            &&
                                            YearMonth.from(b.getDate())
                                                    .equals(month)
                            )

                            .mapToLong(
                                    b -> b.getOutwardQty() == null
                                            ? 0L
                                            : b.getOutwardQty()
                            )

                            .sum();

            // Example:
            // Aug
            // Sep
            // Oct

            String monthLabel =
                    month.format(
                            DateTimeFormatter.ofPattern("MMM")
                    );

            MovementTrendDTO trendDTO =
                    new MovementTrendDTO(
                            monthLabel,
                            inward,
                            outward
                    );

            movementTrend.add(trendDTO);
        }

        dashboard.setMovementTrend(
                movementTrend
        );

// =========================================================
// 8. RECENT STOCK ACTIVITY
// LAST 3 DAYS ONLY
// =========================================================

        LocalDate threeDaysAgo = LocalDate.now().minusDays(2);

        List<RecentTransactionDTO> recentTransactions =
                allTransactions.stream()

                        // Only transactions from the last 3 calendar days
                        .filter(b ->
                                b.getDate() != null &&
                                        !b.getDate().isBefore(threeDaysAgo)
                        )

                        // Newest first
                        .sorted(
                                Comparator
                                        .comparing(
                                                BinCard::getDate,
                                                Comparator.reverseOrder()
                                        )
                                        .thenComparing(
                                                BinCard::getId,
                                                Comparator.reverseOrder()
                                        )
                        )

                        // Maximum 10 activities
                        .limit(10)

                        .map(b -> {

                            long quantity;

                            String type;

                            if (b.getType() == TransactionType.INWARD) {

                                type = "INWARD";

                                quantity = b.getInwardQty() != null
                                        ? b.getInwardQty()
                                        : 0L;

                            } else {

                                type = "OUTWARD";

                                quantity = b.getOutwardQty() != null
                                        ? b.getOutwardQty()
                                        : 0L;
                            }

                            RecentTransactionDTO dto =
                                    new RecentTransactionDTO();

                            dto.setDate(b.getDate());

                            dto.setType(type);

                            dto.setReference(
                                    b.getReference() != null
                                            ? b.getReference()
                                            : ""
                            );

                            dto.setQuantity(quantity);

                            dto.setBalance(
                                    b.getBalance() != null
                                            ? b.getBalance()
                                            : 0L
                            );

                            dto.setRemarks(
                                    b.getRemarks() != null
                                            ? b.getRemarks()
                                            : ""
                            );

                            if (b.getItem() != null) {

                                dto.setItemCode(
                                        b.getItem().getItemCode()
                                );

                                dto.setItemName(
                                        b.getItem().getItemName()
                                );
                            }

                            return dto;
                        })
                        .toList();

        dashboard.setRecentTransactions(recentTransactions);
        // =========================================================
        // 10. MOVEMENT DISTRIBUTION
        // =========================================================

        long inwardTransactions =
                allTransactions.stream()
                        .filter(
                                b -> b.getType()
                                        == TransactionType.INWARD
                        )
                        .count();

        long outwardTransactions =
                allTransactions.stream()
                        .filter(
                                b -> b.getType()
                                        == TransactionType.OUTWARD
                        )
                        .count();

        List<MovementSummaryDTO> movementSummary =
                List.of(

                        new MovementSummaryDTO(
                                "INWARD",
                                totalInward,
                                inwardTransactions
                        ),

                        new MovementSummaryDTO(
                                "OUTWARD",
                                totalOutward,
                                outwardTransactions
                        )
                );

        dashboard.setMovementSummary(
                movementSummary
        );

        return dashboard;
    }
}

