package com.example.inventoryAuth.Service;

import com.example.inventoryAuth.DTO.DashboardResponse;
import com.example.inventoryAuth.Entity.BinCard;
import com.example.inventoryAuth.Entity.Item;
import com.example.inventoryAuth.Entity.Status;
import com.example.inventoryAuth.Entity.TransactionType;
import com.example.inventoryAuth.Repository.BinCardRepository;
import com.example.inventoryAuth.Repository.ItemRepository;
import com.example.inventoryAuth.Repository.StockIssueRepository;
import com.example.inventoryAuth.Repository.StockRepository;
import com.example.inventoryAuth.Repository.StockTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockIssueRepository stockIssueRepository;

    @Autowired
    private StockTransferRepository stockTransferRepository;

    @Autowired
    private BinCardRepository binCardRepository;

    public DashboardResponse getDashboardData() {
        // 1. Calculate Summary metrics
        List<Item> activeItems = itemRepository.findByActive(true);

        long totalItems = activeItems.size();

        long lowStockCount = 0;
        List<DashboardResponse.StockLevel> stockLevels = new ArrayList<>();

        for (Item item : activeItems) {
            long currentStock = stockRepository.findByItem(item)
                    .stream()
                    .mapToLong(s -> s.getCurrentQty() != null ? s.getCurrentQty().longValue() : 0L)
                    .sum();

            if (item.getMinimumLevel() != null && currentStock <= item.getMinimumLevel()) {
                lowStockCount++;
            }

            stockLevels.add(new DashboardResponse.StockLevel(
                    item.getItemCode(),
                    currentStock,
                    item.getMinimumLevel() != null ? item.getMinimumLevel() : 0
            ));
        }

        long pendingIssues = stockIssueRepository.countByStatus(Status.UNAPPROVED);
        long pendingTransfers = stockTransferRepository.countByStatus(Status.UNAPPROVED);
        long pendingRequests = pendingIssues + pendingTransfers;

        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        long thisMonthTransactions = binCardRepository.countByDateBetween(startOfMonth, endOfMonth);

        List<DashboardResponse.DashboardMetric> summary = new ArrayList<>();
        summary.add(new DashboardResponse.DashboardMetric("Total Items", totalItems, "Current inventory", "up", "box"));
        summary.add(new DashboardResponse.DashboardMetric("Low Stock Items", lowStockCount, "Below minimum level", "up", "alert"));
        summary.add(new DashboardResponse.DashboardMetric("Pending Requests", pendingRequests, "Awaiting approval", "down", "request"));
        summary.add(new DashboardResponse.DashboardMetric("This Month", thisMonthTransactions, "Total Transactions", "up", "chart"));

        // 2. Calculate Stock Movement Trend (Last 3 months)
        List<DashboardResponse.StockTrend> trend = new ArrayList<>();
        for (int i = 2; i >= 0; i--) {
            LocalDate targetMonth = today.minusMonths(i);
            LocalDate start = targetMonth.withDayOfMonth(1);
            LocalDate end = targetMonth.with(TemporalAdjusters.lastDayOfMonth());

            List<BinCard> monthRecords = binCardRepository.findByDateBetween(start, end);
            long inwardSum = monthRecords.stream().mapToLong(b -> b.getInwardQty() != null ? b.getInwardQty() : 0L).sum();
            long outwardSum = monthRecords.stream().mapToLong(b -> b.getOutwardQty() != null ? b.getOutwardQty() : 0L).sum();

            String monthLabel = targetMonth.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH));
            trend.add(new DashboardResponse.StockTrend(monthLabel, inwardSum, outwardSum));
        }

        // 3. Get Recent Transactions (Latest 10)
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("date"), Sort.Order.desc("id")));
        List<BinCard> latestBinCards = binCardRepository.findAll(pageable).getContent();

        List<DashboardResponse.RecentTransaction> transactions = latestBinCards.stream().map(b -> {
            String typeStr = b.getType() == TransactionType.INWARD ? "inward" : "outward";
            long qty = b.getType() == TransactionType.INWARD ?
                    (b.getInwardQty() != null ? b.getInwardQty() : 0L) :
                    (b.getOutwardQty() != null ? b.getOutwardQty() : 0L);

            return new DashboardResponse.RecentTransaction(
                    b.getDate() != null ? b.getDate().toString() : "",
                    typeStr,
                    b.getReference() != null ? b.getReference() : "",
                    qty,
                    b.getBalance() != null ? b.getBalance() : 0L,
                    b.getRemarks() != null ? b.getRemarks() : ""
            );
        }).collect(Collectors.toList());

        return new DashboardResponse(summary, stockLevels, trend, transactions);
    }
}
