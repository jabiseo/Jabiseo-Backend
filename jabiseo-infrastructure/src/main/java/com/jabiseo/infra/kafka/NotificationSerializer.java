package com.jabiseo.infra.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import com.jabiseo.domain.notification.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

@Slf4j
public class NotificationSerializer implements Serializer<Notification> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Notification notification) {
        try {
            return objectMapper.writeValueAsBytes(notification);
        } catch (Exception e) {
            log.error("Notification Serialize Error", e);
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
