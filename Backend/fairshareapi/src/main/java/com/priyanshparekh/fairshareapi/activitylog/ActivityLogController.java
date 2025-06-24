package com.priyanshparekh.fairshareapi.activitylog;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/groups/{group-id}/logs")
    ResponseEntity<List<ActivityLog>> getActivityLogs(@PathVariable(value = "group-id") Long groupId) {
        return ResponseEntity.ok(activityLogService.getActivityLogs(groupId));
    }

    @PostMapping("/groups/{group-id}/logs")
    public ResponseEntity<ActivityLog> addActivityLogs(@PathVariable(value = "group-id") Long groupId, @RequestBody ActivityLog activityLog) {
        return ResponseEntity.ok(activityLogService.addActivityLog(activityLog));
    }

}
