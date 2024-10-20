package com.jabiseo.api.member.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameRequest(@NotBlank String nickname) {
}
