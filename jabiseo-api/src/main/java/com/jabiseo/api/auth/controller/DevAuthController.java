package com.jabiseo.api.auth.controller;

import com.jabiseo.api.auth.application.DevLoginHelper;
import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.api.config.deviceinfo.DeviceInfo;
import com.jabiseo.api.config.deviceinfo.RequestDeviceInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Profile({"local", "dev"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DevAuthController {

    private final DevLoginHelper loginHelper;

    @GetMapping("/dev/auth")
    public ResponseEntity<?> devAuth(
            @RequestParam(value = "member-id") @NotBlank String memberId,
            @RequestParam(value = "fcm-token", required = false) String token,
            @RequestDeviceInfo DeviceInfo deviceInfo
    ) {

        LoginResponse result = loginHelper.login(Long.parseLong(memberId), deviceInfo.getDeviceId(), token);
        return ResponseEntity.ok(result);
    }


}
