package com.jabiseo.api.config.deviceinfo;

import lombok.Getter;

@Getter
public class DeviceInfo {

    private String deviceId;

    public DeviceInfo(String deviceId) {
        this.deviceId = deviceId;
    }
}
