package com.jabiseo.notification.consumer;


import com.jabiseo.domain.notification.domain.Notification;
import com.jabiseo.notification.fcm.FcmSender;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class NotificationConsumer {

    private final FcmSender fcmSender;

    @KafkaListener(topics = "${notification.topic}", groupId = "${notification.group-id}", containerFactory = "notificationContainerFactory")
    public void listen(ConsumerRecord<String, Notification> record, Acknowledgment ask) throws Exception {
        fcmSender.sendMessage(record.value());
        ask.acknowledge();
    }
}
