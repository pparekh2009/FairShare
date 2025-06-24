package com.priyanshparekh.fairshareapi.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    List<UserDevice> findAllByUserIdIn(List<Long> userIds);

    void deleteAllByUserIdAndFcmToken(Long l, String fcmToken);
}
