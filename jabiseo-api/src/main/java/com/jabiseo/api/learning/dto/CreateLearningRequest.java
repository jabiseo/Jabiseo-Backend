package com.jabiseo.api.learning.dto;

import com.jabiseo.api.common.validator.EnumValid;
import com.jabiseo.domain.learning.domain.LearningMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateLearningRequest(
        @Min(value = 1L, message = "학습 시간은 0보다 커야 합니다.")
        @Max(value = 1000000000L, message = "학습 시간이 비정상적으로 큽니다.")
        Long learningTime,

        @EnumValid(enumClass = LearningMode.class, message = "학습 모드가 올바르지 않습니다.")
        String learningMode,

        @NotNull(message = "자격증 ID를 입력해야 합니다.")
        Long certificateId,

        @Valid
        @NotEmpty(message = "문제를 풀어야 합니다.")
        List<ProblemResultRequest> problems
) {
}
