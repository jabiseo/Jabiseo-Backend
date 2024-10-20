package com.jabiseo.api.learning.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProblemResultRequest(
        @NotNull(message = "문제 ID를 입력해야 합니다.")
        Long problemId,

        @Min(value = 1L, message = "선지는 1번부터 4번까지 존재합니다.")
        @Max(value = 4L, message = "선지는 1번부터 4번까지 존재합니다.")
        int choice
) {
}
