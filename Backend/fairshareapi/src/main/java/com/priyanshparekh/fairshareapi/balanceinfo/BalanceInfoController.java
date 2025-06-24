package com.priyanshparekh.fairshareapi.balanceinfo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BalanceInfoController {

    private final BalanceInfoService balanceInfoService;

    public BalanceInfoController(BalanceInfoService balanceInfoService) {
        this.balanceInfoService = balanceInfoService;
    }

    @GetMapping("groups/{group-id}/balance-info/{user-id}")
    public ResponseEntity<List<BalanceInfo>> getBalanceInfo(@PathVariable(value = "group-id") Long groupId, @PathVariable(value = "user-id") Long userId) {
        return ResponseEntity.ok(balanceInfoService.getBalanceInfo(groupId, userId));
    }

    @PostMapping("/groups/{group-id}/update-balance-info")
    public ResponseEntity<List<BalanceInfo>> updateBalanceInfo(@RequestBody List<BalanceInfo> newOwesToList, @PathVariable(value = "group-id") Long groupId) {
        return ResponseEntity.ok(balanceInfoService.updateBalanceInfo(newOwesToList, groupId));
    }

    @PostMapping("groups/{group-id}/pay")
    public ResponseEntity<PayData> payUser(@RequestBody PayData payData, @PathVariable(value = "group-id") Long groupId) {
        return ResponseEntity.ok(balanceInfoService.payUser(payData, groupId));
    }
}
