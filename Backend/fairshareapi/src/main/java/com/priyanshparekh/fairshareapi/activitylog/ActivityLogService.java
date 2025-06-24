package com.priyanshparekh.fairshareapi.activitylog;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    ActivityLog addActivityLog(ActivityLog activityLog) {
        return activityLogRepository.save(activityLog);
    }

    List<ActivityLog> getActivityLogs(Long groupId) {
        return activityLogRepository.findAllByGroupId(groupId);
    }
}
