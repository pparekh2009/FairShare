package com.priyanshparekh.fairshareapi.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/groups/{group-id}/balances")
    public ResponseEntity<List<NetBalanceEntry>> getNetBalances(@PathVariable(value = "group-id") Long groupId) {
        return ResponseEntity.ok(dashboardService.getNetBalances(groupId));
    }

    @GetMapping("/groups/{group-id}/stats")
    public ResponseEntity<DashboardDTO> getGroupStats(@PathVariable(value = "group-id") Long groupId) {
        DashboardDTO stats = dashboardService.getGroupExpenseStats(groupId);
        return ResponseEntity.ok(stats);
    }
}
