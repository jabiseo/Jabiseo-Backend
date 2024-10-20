package com.jabiseo.notification.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class FcmSender {


    public void sendMessage(com.jabiseo.domain.notification.domain.Notification notification){
        Notification fcmNotification = Notification
                .builder()
                .setTitle(notification.getPushType().getTitle())
                .setBody(notification.getPushType().getMessage())
                .build();

        Message message = Message.builder()
                .setToken(notification.getToken())
                .putData("certificateId", notification.getRedirectId().toString())
                .putData("type", notification.getPushType().getType())
                .setNotification(fcmNotification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info(response);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
