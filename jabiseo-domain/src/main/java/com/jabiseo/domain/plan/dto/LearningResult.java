package com.jabiseo.domain.plan.dto;

import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.learning.dto.LearningWithSolvingCountQueryDto;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class LearningResult {


    private LearningMode mode;
    private Long learningTime;
    private Long solvingCount;

    public static LearningResult from(LearningWithSolvingCountQueryDto queryDto) {
        return LearningResult.builder()
                .mode(queryDto.getMode())
                .learningTime(queryDto.getLearningTime())
                .solvingCount(queryDto.getSolvingCount())
                .build();
    }

    public static LearningResult valueOf(Learning learning, long count) {
        return LearningResult.builder()
                .mode(learning.getMode())
                .learningTime(learning.getLearningTime())
                .solvingCount(count)
                .build();
    }
}
