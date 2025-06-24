package com.priyanshparekh.fairshareapi.notification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_devices")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String fcmToken;
    private String endpointArn;

}
