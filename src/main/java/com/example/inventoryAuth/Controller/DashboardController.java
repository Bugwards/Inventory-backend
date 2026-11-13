package com.example.inventoryAuth.Controller;

import com.example.inventoryAuth.DTO.DashboardAnalyticsDTO;
import com.example.inventoryAuth.DTO.DashboardResponse;
import com.example.inventoryAuth.Service.BinCardService;
import com.example.inventoryAuth.Service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    @Autowired
    private BinCardService binCardService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping({"/dashboard", "/api/dashboard"})
    public DashboardResponse getDashboardData() {
        return dashboardService.getDashboardData();
    }

    @GetMapping("/api/dashboard/analytics")
    public ResponseEntity<DashboardAnalyticsDTO> getDashboardAnalytics() {

        DashboardAnalyticsDTO dashboard =
                binCardService.getDashboardAnalytics();

        return ResponseEntity.ok(dashboard);
    }
}
