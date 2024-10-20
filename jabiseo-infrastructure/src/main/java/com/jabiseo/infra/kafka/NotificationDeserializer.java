package com.jabiseo.infra.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import com.jabiseo.domain.notification.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class NotificationDeserializer implements Deserializer<Notification> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Notification deserialize(String topic, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, Notification.class);
        } catch (Exception e) {
            log.error("Notification Deserialize Error", e);
            throw new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
