package com.jabiseo.infra.kafka;

import com.jabiseo.domain.notification.domain.Notification;
import com.jabiseo.domain.notification.domain.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducerImpl implements NotificationSender {

    private final KafkaTemplate<String, Notification> kafkaTemplate;

    @Override
    public void send(String topic, Notification notification) {
        kafkaTemplate.send(topic, notification.getPushType().getType(), notification)
                .thenRun(() -> log.info("Sent notification: {}", notification.getMemberId()));
    }
}
