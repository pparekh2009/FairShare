package com.priyanshparekh.fairshareapi.expense;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ExpenseController {

    private final ExpenseService expenseService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/groups/{group-id}/expenses")
    public ResponseEntity<ExpenseDTO> addExpense(@PathVariable(value = "group-id") Long groupId, @RequestBody ExpenseDTO expenseDTO) {
        return ResponseEntity.ok(expenseService.addExpense(expenseDTO));
    }

    @GetMapping("/groups/{group-id}/expenses")
    public ResponseEntity<List<ExpenseDTO>> getExpenses(@PathVariable(value = "group-id") Long groupId) {
        return ResponseEntity.ok(expenseService.getExpenses(groupId));
    }

    @PostMapping("/groups/{group-id}/receipts")
    public ResponseEntity<UploadResponse> uploadReceipt(@RequestParam("file") MultipartFile file) {
        logger.info("expenseController: uploadReceipt: file: {}", file);
        return ResponseEntity.ok(expenseService.uploadReceipt(file));
    }
}
