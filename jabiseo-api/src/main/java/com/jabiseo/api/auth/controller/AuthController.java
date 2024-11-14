package com.jabiseo.api.auth.controller;

import com.jabiseo.api.auth.dto.LoginRequest;
import com.jabiseo.api.auth.dto.LoginResponse;
import com.jabiseo.api.auth.dto.ReissueRequest;
import com.jabiseo.api.auth.dto.ReissueResponse;
import com.jabiseo.api.auth.application.usecase.LoginUseCase;
import com.jabiseo.api.auth.application.usecase.LogoutUseCase;
import com.jabiseo.api.auth.application.usecase.ReissueUseCase;
import com.jabiseo.api.auth.application.usecase.WithdrawUseCase;
import com.jabiseo.api.config.auth.AuthMember;
import com.jabiseo.api.config.auth.AuthenticatedMember;
import com.jabiseo.api.config.deviceinfo.DeviceInfo;
import com.jabiseo.api.config.deviceinfo.RequestDeviceInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    private final ReissueUseCase reissueUseCase;

    private final LogoutUseCase logoutUseCase;

    private final WithdrawUseCase withdrawUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            @RequestDeviceInfo DeviceInfo deviceInfo
    ) {
        LoginResponse result = loginUseCase.execute(loginRequest, deviceInfo.getDeviceId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(
            @Valid @RequestBody ReissueRequest request,
            @AuthenticatedMember AuthMember member,
            @RequestDeviceInfo DeviceInfo deviceInfo
    ) {
        ReissueResponse result = reissueUseCase.execute(request, member.getMemberId(), deviceInfo.getDeviceId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticatedMember AuthMember member,
            @RequestDeviceInfo DeviceInfo deviceInfo
    ) {
        logoutUseCase.execute(member.getMemberId(), deviceInfo.getDeviceId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw() {
        withdrawUseCase.execute();
        return ResponseEntity.noContent().build();
    }
}
