package com.priyanshparekh.fairshareapi.notification;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class NotificationRequest {

    private List<Long> userIds;
    private Map<String, String> messageData;

}
