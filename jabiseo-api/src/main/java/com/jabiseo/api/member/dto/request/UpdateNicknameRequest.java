package com.jabiseo.api.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameRequest(@NotBlank String nickname) {
}
