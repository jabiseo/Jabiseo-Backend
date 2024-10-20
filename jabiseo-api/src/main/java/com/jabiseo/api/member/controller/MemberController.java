package com.jabiseo.api.member.controller;

import com.jabiseo.api.member.application.usecase.*;
import com.jabiseo.api.member.dto.*;
import com.jabiseo.api.config.auth.AuthMember;
import com.jabiseo.api.config.auth.AuthenticatedMember;
import com.jabiseo.api.member.application.usecase.*;
import com.jabiseo.api.member.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/myinfo")
public class MemberController {

    private final FindMyInfoUseCase findMyInfoUseCase;

    private final FindMyCurrentCertificateUseCase findMyCurrentCertificateUseCase;

    private final UpdateMyCurrentCertificateUseCase updateMyCurrentCertificateUseCase;

    private final UpdateNicknameUseCase updateNicknameUseCase;

    private final UpdateProfileImageUseCase updateProfileImageUseCase;

    @GetMapping
    public ResponseEntity<FindMyInfoResponse> findMyInfo(
            @AuthenticatedMember AuthMember member
    ) {
        FindMyInfoResponse result = findMyInfoUseCase.execute(member.getMemberId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/certificates")
    public ResponseEntity<FindMyCurrentCertificateResponse> findMyCurrentCertificate(
            @AuthenticatedMember AuthMember member
    ) {
        FindMyCurrentCertificateResponse result = findMyCurrentCertificateUseCase.execute(member.getMemberId());
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/certificates")
    public ResponseEntity<Void> updateMyCurrentCertificate(
            @RequestBody UpdateMyCurrentCertificateRequest request,
            @AuthenticatedMember AuthMember member
    ) {
        updateMyCurrentCertificateUseCase.execute(member.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/nickname")
    public ResponseEntity<UpdateNicknameResponse> updateMyNickname(
            @AuthenticatedMember AuthMember member,
            @Valid @RequestBody UpdateNicknameRequest request
    ) {
        UpdateNicknameResponse result = updateNicknameUseCase.execute(member.getMemberId(), request);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/image")
    public ResponseEntity<UpdateProfileImageResponse> updateImage(
            @AuthenticatedMember AuthMember member,
            @Valid @ModelAttribute UpdateProfileImageRequest request
    ) {
        UpdateProfileImageResponse result = updateProfileImageUseCase.execute(member.getMemberId(), request);
        return ResponseEntity.ok(result);
    }


}
