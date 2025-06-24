package com.priyanshparekh.fairshareapi.data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/data")
    public DataDTO getAllData() {
        return dataService.getAllData();
    }

}
