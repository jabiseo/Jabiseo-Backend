package com.jabiseo.api.auth.dto;

import com.jabiseo.api.common.validator.EnumValid;
import com.jabiseo.domain.member.domain.OauthServer;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull
        String idToken,
        @EnumValid(enumClass = OauthServer.class, message = "oauthServer Type이 올바르지 않습니다.")
        String oauthServer
) {
}
