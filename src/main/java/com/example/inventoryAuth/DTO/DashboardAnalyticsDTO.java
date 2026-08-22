package com.example.inventoryAuth.DTO;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class DashboardAnalyticsDTO {

    private long totalItems;
    private long totalStock;
    private long lowStockItems;
    private long fastMovingItems;
    private long slowMovingItems;

    private long totalInward;
    private long totalOutward;

    private double stockHealthPercentage;

    private List<StockLevelDTO> stockLevels;
    private List<MovementTrendDTO> movementTrend;
    private List<RecentTransactionDTO> recentTransactions;
    private List<MovementSummaryDTO> movementSummary;

}