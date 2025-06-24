package com.priyanshparekh.fairshareapi.expense;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserAmountRepository userAmountRepository;

    private final BalanceInfoService balanceInfoService;

    @Autowired
    private AmazonS3 amazonS3;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ExpenseService(ExpenseRepository expenseRepository, UserAmountRepository userAmountRepository, BalanceInfoService balanceInfoService) {
        this.expenseRepository = expenseRepository;
        this.userAmountRepository = userAmountRepository;
        this.balanceInfoService = balanceInfoService;
    }

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        Expense expense = getExpenseFromDTO(expenseDTO);
        Expense savedExpense = expenseRepository.save(expense);

        List<UserAmount> userAmountList = getUserAmountsFromDTO(expenseDTO, savedExpense);
        List<UserAmount> savedUserAmountList = userAmountRepository.saveAll(userAmountList);

        balanceInfoService.addBalanceInfo(expenseDTO);

        return getExpenseDTO(savedExpense);
    }

    private ExpenseDTO getExpenseDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .groupId(expense.getGroupId())
                .name(expense.getName())
                .note(expense.getNote())
                .amount(expense.getAmount())
                .paidBy(expense.getPaidBy())
                .createdAt(expense.getCreatedAt())
                .receiptUrl(expense.getReceiptUrl())
                .build();
    }

    private Expense getExpenseFromDTO(ExpenseDTO expenseDTO) {
        return Expense.builder()
                .groupId(expenseDTO.getGroupId())
                .name(expenseDTO.getName())
                .note(expenseDTO.getNote())
                .amount(expenseDTO.getAmount())
                .paidBy(expenseDTO.getPaidBy())
                .createdAt(expenseDTO.getCreatedAt())
                .receiptUrl(expenseDTO.getReceiptUrl())
                .build();
    }

    private List<UserAmount> getUserAmountsFromDTO(ExpenseDTO expenseDTO, Expense savedExpense) {
        int groupSize = expenseDTO.getSplitBetween().size();
        double totalAmount = expenseDTO.getAmount();
        double individualAmount = totalAmount / groupSize;

        List<UserAmount> userAmountList = new ArrayList<>();
        for (int i = 0; i < groupSize; i++) {
            UserAmount userAmount = new UserAmount();
            userAmount.setUserId(expenseDTO.getSplitBetween().get(i).getId());
            userAmount.setExpenseId(savedExpense.getId());
            userAmount.setAmount(individualAmount);

            userAmountList.add(userAmount);
        }
        return userAmountList;
    }

    public List<ExpenseDTO> getExpenses(Long groupId) {
        List<Expense> expenseList = expenseRepository.findAllByGroupId(groupId);

        return getExpenseDTOList(expenseList);
    }

    private List<ExpenseDTO> getExpenseDTOList(List<Expense> expenseList) {

        return expenseList.stream()
                .map(expense -> {
                    return new ExpenseDTO(expense.getId(), expense.getGroupId(), expense.getName(), expense.getAmount(), expense.getNote(), expense.getPaidBy(), expense.getCreatedAt(), expense.getReceiptUrl(), null);
                })
                .collect(Collectors.toList());
    }

    public UploadResponse uploadReceipt(MultipartFile file) {
        logger.info("expenseService: uploadReceipt: original file name: {}", file.getOriginalFilename());
        logger.info("expenseService: uploadReceipt: size: {}", file.getSize());

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            String fileName = getFileName(file.getOriginalFilename());

            PutObjectResult result = amazonS3.putObject(new PutObjectRequest(
                    "fairshare-bucket",
                    fileName,
                    file.getInputStream(),
                    metadata
            ));

            if (result.getETag() != null && !result.getETag().isEmpty()) {
                return new UploadResponse(true, fileName);
            }

            return new UploadResponse(false, "Error uploading file");
        } catch (Exception e) {
            return new UploadResponse(false, e.getLocalizedMessage());
        }
    }

    private String getFileName(String originalFilename) {
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        return UUID.randomUUID() + extension;
//        String key = "receipts/" + uniqueFilename;
//
//        return key; // return this and store in D
    }
}
