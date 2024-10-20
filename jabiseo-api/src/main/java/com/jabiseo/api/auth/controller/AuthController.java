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
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        LoginResponse result = loginUseCase.execute(loginRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueResponse> reissue(
            @Valid @RequestBody ReissueRequest request,
            @AuthenticatedMember AuthMember member
    ) {
        ReissueResponse result = reissueUseCase.execute(request, member.getMemberId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticatedMember AuthMember member
    ) {
        logoutUseCase.execute(member.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw() {
        withdrawUseCase.execute();
        return ResponseEntity.noContent().build();
    }
}
