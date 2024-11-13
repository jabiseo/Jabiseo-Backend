package com.jabiseo.api.auth.controller;

import com.jabiseo.api.auth.application.DevLoginHelper;
import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.api.config.deviceinfo.DeviceInfo;
import com.jabiseo.api.config.deviceinfo.RequestDeviceInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Profile({"local", "dev"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DevAuthController {

    private final Environment environment;
    private final DevLoginHelper loginHelper;
    private static final String LIMIT_PROFILE = "local";

    @GetMapping("/dev/auth")
    public ResponseEntity<?> devAuth(
            @RequestParam(value = "member-id") @NotBlank String memberId,
            @RequestDeviceInfo DeviceInfo info
    ) {

        LoginResponse result = loginHelper.login(Long.parseLong(memberId));
        return ResponseEntity.ok(result);
    }


}
