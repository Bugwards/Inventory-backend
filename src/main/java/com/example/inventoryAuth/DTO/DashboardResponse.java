package com.example.inventoryAuth.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private List<DashboardMetric> summary;
    private List<StockLevel> stockLevels;
    private List<StockTrend> trend;
    private List<RecentTransaction> transactions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardMetric {
        private String label;
        private Long value;
        private String trend;
        private String trendType; // "up" | "down"
        private String icon; // "box" | "alert" | "request" | "chart"
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockLevel {
        private String name;
        private Long stock;
        private Integer minLevel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockTrend {
        private String month;
        private Long inward;
        private Long outward;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTransaction {
        private String date;
        private String type; // "inward" | "outward"
        private String reference;
        private Long quantity;
        private String remarks;
        private long balance;

        public RecentTransaction(String date, String typeStr, String reference, long qty, long l, String s) {
        }
    }
}
