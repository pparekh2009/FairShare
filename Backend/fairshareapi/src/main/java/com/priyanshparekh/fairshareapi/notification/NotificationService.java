package com.priyanshparekh.fairshareapi.notification;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final AmazonSNS snsClient;
    private final UserDeviceRepository userDeviceRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${aws.sns.platformApplicationArn}")
    private String platformApplicationArn;

    public NotificationService(AmazonSNS snsClient, UserDeviceRepository userDeviceRepository) {
        this.snsClient = snsClient;
        this.userDeviceRepository = userDeviceRepository;
    }

    public UserDevice registerDevice(UserDevice userDevice) {
        Long userId = userDevice.getUserId();
        String fcmToken = userDevice.getFcmToken();
        logger.info("notificationService: registerDevice: fcmToken: {}", fcmToken);

        logger.info("notificationService: registerDevice: platform arn: {}", platformApplicationArn);
        try {
            CreatePlatformEndpointRequest endpointRequest = new CreatePlatformEndpointRequest()
                    .withPlatformApplicationArn(platformApplicationArn)
                    .withToken(fcmToken);

            CreatePlatformEndpointResult result = snsClient.createPlatformEndpoint(endpointRequest);
            String endpointArn = result.getEndpointArn();

            userDevice.setEndpointArn(endpointArn);

            return userDeviceRepository.save(userDevice);
        } catch (InvalidParameterException e) {
            logger.info("notificationService: registerDevice: exception: {}", e.getMessage());
            return null;
        }
    }

    public void sendNotification(NotificationRequest notificationRequest) {
        List<Long> userIds = notificationRequest.getUserIds();

        List<UserDevice> userDevices = userDeviceRepository.findAllByUserIdIn(userIds);

//        Map<Long, List<String>> userDeviceMap = new HashMap<>();

//        for (UserDevice userDevice : userDevices) {
//            if (userDeviceMap.get(userDevice.getUserId()) == null) {
//                userDeviceMap.put(userDevice.getUserId(), new ArrayList<>(List.of(userDevice.getEndpointArn())));
//            } else {
//                List<String> updatedArnList = userDeviceMap.get(userDevice.getUserId());
//                updatedArnList.add(userDevice.getEndpointArn());
//                userDeviceMap.put(userDevice.getUserId(), updatedArnList);
//            }
//        }
        for (UserDevice userDevice : userDevices) {
            try {
                Map<Long, Object> fcmMessage = new HashMap<>();
                Map<String, String> data = new HashMap<>(notificationRequest.getMessageData());

                fcmMessage.put(userDevice.getUserId(), data);

                Map<String, Object> gcmPayload = new HashMap<>();
                Map<String, String> notification = new HashMap<>();
                notification.put("title", data.get("title"));
                notification.put("body", data.get("message"));
                notification.put("sound", "default");

                gcmPayload.put("notification", notification);
                gcmPayload.put("data", data);

                ObjectMapper objectMapper = new ObjectMapper();

                Map<String, String> finalPayload = new HashMap<>();
                finalPayload.put("default", "You have a new message!");
                finalPayload.put("GCM", objectMapper.writeValueAsString(gcmPayload));

                logger.info("notificationService: sendNotification: fcmMessage: {}", fcmMessage);
                logger.info("notificationService: sendNotification: data: {}", data);

                String messageJson = new ObjectMapper().writeValueAsString(finalPayload);

                logger.info("notificationService: sendNotification: messageJson: {}", messageJson);

                PublishRequest request = new PublishRequest()
                        .withTargetArn(userDevice.getEndpointArn())
                        .withMessage(messageJson)
                        .withMessageStructure("json");

                PublishResult result = snsClient.publish(request);

                logger.info("notificationService: sendNotification: messageId: {}", result.getMessageId());
            } catch (Exception e) {
                logger.info("notificationService: sendNotification: exception: {}", e.getMessage());
            }
        }
    }

    @Transactional
    public void unregisterDevice(String userId, String fcmToken) {
        logger.info("notificationService: unregisterDevice: userId: {}", userId);
        logger.info("notificationService: unregisterDevice: fcmToken: {}", fcmToken);
        userDeviceRepository.deleteAllByUserIdAndFcmToken(Long.parseLong(userId), fcmToken);
    }
}
