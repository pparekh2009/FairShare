package com.priyanshparekh.fairshareapi.exportdata;

import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfo;
import com.priyanshparekh.fairshareapi.expense.Expense;
import com.priyanshparekh.fairshareapi.user.User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExportToExcelService {

    public void generateExcel(HttpServletResponse response, ExportDataDTO data) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=data.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Map<Long, String> usernames = getUsernames(data.getUserList());

        int rowIdx = 0;

        List<Expense> expenseList = data.getExpenseList();
        Row expenseHeader = sheet.createRow(rowIdx++);
        expenseHeader.createCell(0).setCellValue("ID");
        expenseHeader.createCell(1).setCellValue("Name");
        expenseHeader.createCell(2).setCellValue("Amount");
        expenseHeader.createCell(3).setCellValue("Paid By");
        expenseHeader.createCell(4).setCellValue("Note");

        for (Expense expense : expenseList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(expense.getId());
            row.createCell(1).setCellValue(expense.getName());
            row.createCell(2).setCellValue(expense.getAmount());
            row.createCell(3).setCellValue(usernames.get(expense.getPaidBy()));
            row.createCell(4).setCellValue(expense.getNote());
        }

        rowIdx++;

        List<BalanceInfo> balanceInfoList = data.getBalanceInfoList();
        Row balanceInfoHeader = sheet.createRow(rowIdx++);
        balanceInfoHeader.createCell(0).setCellValue("ID");
        balanceInfoHeader.createCell(1).setCellValue("User");
        balanceInfoHeader.createCell(2).setCellValue("Direction");
        balanceInfoHeader.createCell(3).setCellValue("Other User");
        balanceInfoHeader.createCell(4).setCellValue("Amount");

        for (BalanceInfo balanceInfo : balanceInfoList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(balanceInfo.getId());
            row.createCell(1).setCellValue(usernames.get(balanceInfo.getUserId()));
            row.createCell(2).setCellValue(String.valueOf(balanceInfo.getDirection()));
            row.createCell(3).setCellValue(usernames.get(balanceInfo.getOtherUserId()));
            row.createCell(4).setCellValue(balanceInfo.getAmount());
        }

        try {
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Long, String> getUsernames(List<User> users) {
        Map<Long, String> usernames = new HashMap<>();
        for (User user : users) {
            usernames.put(user.getId(), user.getName());
        }

        return usernames;
    }


//    public void writeTableData(ExportDataDTO data) {
//        // data
//        List<Expense> expenseList = data.getExpenseList();
//        List<BalanceInfo> balanceInfoList = data.getBalanceInfoList();
//        List<User> userList = data.getUserList();
//
//        Map<Long, String> usernames = new HashMap<>();
//        for (User user : userList) {
//            usernames.put(user.getId(), user.getName());
//        }
//
//        // font style content
//        CellStyle style = getFontContentExcel();
//
//        // starting write on row
//        int startRow = 2;
//
//        // write content
//        for (Expense expense : expenseList) {
//            Row row = sheet.createRow(startRow++);
//            int columnCount = 0;
//            createCell(row, columnCount++, expense.getId(), style);
//            createCell(row, columnCount++, expense.getName(), style);
//            createCell(row, columnCount++, expense.getAmount(), style);
//            createCell(row, columnCount++, expense.getNote(), style);
//            createCell(row, columnCount++, usernames.get(expense.getPaidBy()), style);
//        }
//
//        startRow += 2;
//
//        for (BalanceInfo balanceInfo : balanceInfoList) {
//            Row row = sheet.createRow(startRow++);
//            int columnCount = 0;
//            createCell(row, columnCount++, usernames.get(balanceInfo.getUserId()), style);
//            createCell(row, columnCount++, balanceInfo.getDirection(), style);
//            createCell(row, columnCount++, usernames.get(balanceInfo.getOtherUserId()), style);
//            createCell(row, columnCount++, balanceInfo.getAmount(), style);
//        }
//    }

//    public void exportToExcel(HttpServletResponse response, ExportDataDTO data) throws IOException {
//        newReportExcel();
//
//        // response  writer to excel
//        response = initResponseForExportExcel(response, "UserExcel");
//        ServletOutputStream outputStream = response.getOutputStream();
//
//
//        // write sheet, title & header
//        String[] headers = new String[]{"No", "username", "Password", "Roles", "Permission", "Active", "Bloked", "Created By", "Created Date", "Update By", "Update Date"};
//        writeTableHeaderExcel("Sheet User", "Report User", headers);
//
//        // write content row
//        writeTableData(data);
//
//        workbook.write(outputStream);
//        workbook.close();
//        outputStream.close();
//    }

}
