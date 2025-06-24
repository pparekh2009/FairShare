package com.priyanshparekh.fairshareapi.exportdata;

import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfo;
import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfoRepository;
import com.priyanshparekh.fairshareapi.expense.Expense;
import com.priyanshparekh.fairshareapi.expense.ExpenseRepository;
import com.priyanshparekh.fairshareapi.group.GroupService;
import com.priyanshparekh.fairshareapi.user.User;
import com.priyanshparekh.fairshareapi.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExportDataService {

    private final ExportToPdfService userExportToPdfService;
    private final ExportToExcelService userExportToExcelService;

    private final ExpenseRepository expenseRepository;
    private final BalanceInfoRepository balanceInfoRepository;
    private final GroupService groupService;

    public ExportDataService(ExportToPdfService userExportToPdfService, ExportToExcelService userExportToExcelService, ExpenseRepository expenseRepository, BalanceInfoRepository balanceInfoRepository, GroupService groupService) {
        this.userExportToPdfService = userExportToPdfService;
        this.userExportToExcelService = userExportToExcelService;
        this.expenseRepository = expenseRepository;
        this.balanceInfoRepository = balanceInfoRepository;
        this.groupService = groupService;
    }

    public void exportToPdf(HttpServletResponse response, Long groupId) throws IOException {
        // get all user
        List<Expense> expenses = expenseRepository.findAllByGroupId(groupId);
        List<BalanceInfo> balanceInfoList = balanceInfoRepository.findAllByGroupId(groupId);
        List<User> users = groupService.getMembers(groupId);

        ExportDataDTO exportDataDTO = new ExportDataDTO(expenses, balanceInfoList, users);

        // export to pdf
        userExportToPdfService.generatePdf(response, exportDataDTO);
    }


    public void exportToExcel(HttpServletResponse response, Long groupId) throws IOException {
        // get all user
        List<Expense> expenses = expenseRepository.findAllByGroupId(groupId);
        List<BalanceInfo> balanceInfoList = balanceInfoRepository.findAllByGroupId(groupId);
        List<User> users = groupService.getMembers(groupId);

        ExportDataDTO exportDataDTO = new ExportDataDTO(expenses, balanceInfoList, users);

        // export to pdf
        userExportToExcelService.generateExcel(response, exportDataDTO);

    }
}
