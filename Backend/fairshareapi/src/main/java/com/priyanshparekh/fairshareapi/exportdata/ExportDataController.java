package com.priyanshparekh.fairshareapi.exportdata;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ExportDataController {

    private final ExportDataService userReportService;

    public ExportDataController(ExportDataService userReportService) {
        this.userReportService = userReportService;
    }

    @GetMapping("/groups/{group-id}/export/pdf")
    public void exportToPdf(HttpServletResponse response, @PathVariable(value = "group-id") Long groupId) throws IOException {
        this.userReportService.exportToPdf(response, groupId);
    }


    @GetMapping("/groups/{group-id}/export/excel")
    public void exportToExcel(HttpServletResponse response, @PathVariable(value = "group-id") Long groupId) throws IOException {
        this.userReportService.exportToExcel(response, groupId);
    }

}
