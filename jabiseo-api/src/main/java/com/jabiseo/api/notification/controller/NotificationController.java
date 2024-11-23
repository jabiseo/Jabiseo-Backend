package com.jabiseo.api.notification.controller;


import com.jabiseo.api.config.auth.AuthMember;
import com.jabiseo.api.config.auth.AuthenticatedMember;
import com.jabiseo.domain.notification.domain.Notification;
import com.jabiseo.domain.notification.domain.NotificationSender;
import com.jabiseo.domain.notification.domain.PushType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/*
 * 클라이언트를 위한 Test API
 */

@RestController
@RequiredArgsConstructor
@Profile({"dev", "local"})
public class NotificationController {

    private final NotificationSender notificationSender;

    @PostMapping("/api/notification/test")
    public ResponseEntity<Void> sendNotification(
            @AuthenticatedMember AuthMember member,
            @RequestParam String token,
            @RequestParam Long certificateId
    ) {
        notificationSender.send("test", new Notification(null, member.getMemberId(), PushType.PLAN, certificateId, token));
        return ResponseEntity.ok().build();
    }
}
