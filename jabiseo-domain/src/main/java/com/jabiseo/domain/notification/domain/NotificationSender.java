package com.jabiseo.domain.notification.domain;

public interface NotificationSender {

    void send(String topic, Notification notification);
}
