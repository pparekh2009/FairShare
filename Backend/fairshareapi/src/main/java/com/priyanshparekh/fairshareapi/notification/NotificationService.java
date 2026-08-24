package com.priyanshparekh.fairshareapi.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {

    private final UserDeviceRepository userDeviceRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public NotificationService(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    public void sendNotification(NotificationRequest notificationRequest) {
        List<Long> userIds = notificationRequest.getUserIds();

        List<UserDevice> userDevices = userDeviceRepository.findAllByUserIdIn(userIds);

        for (UserDevice userDevice : userDevices) {
            try {
                Map<String, String> data = new HashMap<>(notificationRequest.getMessageData());

                Message message = Message.builder()
                        .setToken(userDevice.getFcmToken())
                        .putAllData(data)
                        .setNotification(
                                Notification.builder()
                                        .setTitle(data.get("title"))
                                        .setBody(data.get("message"))
                                        .build()
                        )
                        .build();
                String result = FirebaseMessaging.getInstance().send(message);

                logger.info("notificationService: sendNotification: data: {}", data);

                logger.info("notificationService: sendNotification: messageId: {}", result);
            } catch (Exception e) {
                logger.info("notificationService: sendNotification: exception: {}", e.getMessage());
            }
        }
    }

    @Transactional
    public void unregisterDevice(String userId, String fcmToken) {
        log.info("notificationService: unregisterDevice: userId: {}", userId);
        log.info("notificationService: unregisterDevice: fcmToken: {}", fcmToken);
        userDeviceRepository.deleteAllByUserIdAndFcmToken(Long.parseLong(userId), fcmToken);
    }
}
