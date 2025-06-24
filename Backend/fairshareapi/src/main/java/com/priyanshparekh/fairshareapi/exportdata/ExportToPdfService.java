package com.priyanshparekh.fairshareapi.exportdata;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.priyanshparekh.fairshareapi.balanceinfo.BalanceInfo;
import com.priyanshparekh.fairshareapi.expense.Expense;
import com.priyanshparekh.fairshareapi.user.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExportToPdfService {


    public void generatePdf(HttpServletResponse response, ExportDataDTO dataDTO) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=data.pdf");

        Map<Long, String> usernames = getUsernames(dataDTO.getUserList());

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        List<Expense> expenseList = dataDTO.getExpenseList();
        document.add(new Paragraph("Expense Table"));
        PdfPTable expenseTable = new PdfPTable(5);
        expenseTable.addCell("ID");
        expenseTable.addCell("Name");
        expenseTable.addCell("Amount");
        expenseTable.addCell("Paid By");
        expenseTable.addCell("Note");

        for (Expense expense : expenseList) {
            expenseTable.addCell(expense.getId().toString());
            expenseTable.addCell(expense.getName());
            expenseTable.addCell(expense.getAmount().toString());
            expenseTable.addCell(usernames.get(expense.getPaidBy()));
            expenseTable.addCell(expense.getNote());
        }
        document.add(expenseTable);

        document.add(new Paragraph("\n"));

        List<BalanceInfo> balanceInfoList = dataDTO.getBalanceInfoList();
        document.add(new Paragraph("Balance Info"));
        PdfPTable balanceInfoTable = new PdfPTable(5);
        balanceInfoTable.addCell("ID");
        balanceInfoTable.addCell("User");
        balanceInfoTable.addCell("Direction");
        balanceInfoTable.addCell("Other User");
        balanceInfoTable.addCell("Amount");

        for (BalanceInfo balanceInfo : balanceInfoList) {
            balanceInfoTable.addCell(balanceInfo.getId().toString());
            balanceInfoTable.addCell(usernames.get(balanceInfo.getUserId()));
            balanceInfoTable.addCell(String.valueOf(balanceInfo.getDirection()));
            balanceInfoTable.addCell(usernames.get(balanceInfo.getOtherUserId()));
            balanceInfoTable.addCell(balanceInfo.getAmount().toString());
        }
        document.add(balanceInfoTable);

        document.close();
    }

    private Map<Long, String> getUsernames(List<User> users) {
        Map<Long, String> usernames = new HashMap<>();
        for (User user : users) {
            usernames.put(user.getId(), user.getName());
        }

        return usernames;
    }


//    public void writeTableData(PdfPTable table, Object data) {
//        List<UserDTO> list = (List<UserDTO>) data;
//
//        // for auto wide by paper  size
//        table.setWidthPercentage(100);
//        // cell
//        PdfPCell cell = new PdfPCell();
//        int number = 0;
//        for (UserDTO item : list) {
//            number += 1;
//            cell.setPhrase(new Phrase(String.valueOf(number), getFontContent()));
//            table.addCell(cell);
//
//            cell.setPhrase(new Phrase(item.getUsername(), getFontContent()));
//            table.addCell(cell);
//
//            cell.setPhrase(new Phrase(item.getRoles(), getFontContent()));
//            table.addCell(cell);
//
//            cell.setPhrase(new Phrase(item.getPermissions(), getFontContent()));
//            table.addCell(cell);
//
//            String active = item.getActive() == 1 ? "Active" : "Non Active";
//            cell.setPhrase(new Phrase(active, getFontContent()));
//            table.addCell(cell);
//
//            String blocked = item.getBlocked() == 1 ? "Blocked" : "Non Blocked";
//            cell.setPhrase(new Phrase(blocked, getFontContent()));
//            table.addCell(cell);
//
//            cell.setPhrase(new Phrase(item.getCreatedBy(), getFontContent()));
//            table.addCell(cell);
//
//            cell.setPhrase(new Phrase(item.getCreatedDate().toString(), getFontContent()));
//            table.addCell(cell);
//
//            cell.setPhrase(new Phrase(item.getUpdatedBy(), getFontContent()));
//            table.addCell(cell);
//
//            cell.setPhrase(new Phrase(item.getUpdatedDate().toString(), getFontContent()));
//            table.addCell(cell);
//        }
//
//    }
//
//    public void exportToPDF(HttpServletResponse response, Object data) throws IOException {
//
//
//        // init respose
//        response = initResponseForExportPdf(response, "USER");
//
//        // define paper size
//        Document document = new Document(PageSize.A4);
//        PdfWriter.getInstance(document, response.getOutputStream());
//
//        // start document
//        document.open();
//
//        // title
//        Paragraph title = new Paragraph("Report User", getFontTitle());
//        title.setAlignment(Paragraph.ALIGN_CENTER);
//        document.add(title);
//
//        // subtitel
//        Paragraph subtitel = new Paragraph("Report Date : 09/12/2022", getFontSubtitle());
//        subtitel.setAlignment(Paragraph.ALIGN_LEFT);
//        document.add(subtitel);
//
//        enterSpace(document);
//
//        // table header
//        String[] headers = new String[]{"No", "username", "Roles", "Permission", "Active", "Bloked", "Created By", "Created Date", "Update By", "Update Date"};
//        PdfPTable tableHeader = new PdfPTable(10);
//        writeTableHeaderPdf(tableHeader, headers);
//        document.add(tableHeader);
//
//        // table content
//        PdfPTable tableData = new PdfPTable(10);
//        writeTableData(tableData, data);
//        document.add(tableData);
//
//        document.close();
//    }

}
