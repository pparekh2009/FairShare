package com.priyanshparekh.fairshareapi.notification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDevice> registerDevice(@RequestBody UserDevice userDevice) {
        return ResponseEntity.ok(notificationService.registerDevice(userDevice));
    }

    @PostMapping("/unregister")
    public ResponseEntity<String> unregisterDevice(@RequestParam(value = "id") String userId, @RequestParam(value = "token") String fcmToken) {
        notificationService.unregisterDevice(userId, fcmToken);
        return ResponseEntity.ok("Device Unregistered");
    }

    @PostMapping("/notify")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationService.sendNotification(notificationRequest);
        return ResponseEntity.ok("Notification Sent");
    }
}