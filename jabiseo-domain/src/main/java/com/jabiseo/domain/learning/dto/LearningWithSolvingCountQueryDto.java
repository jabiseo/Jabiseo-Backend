package com.jabiseo.domain.learning.dto;

import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningWithSolvingCountQueryDto {

    private LearningMode mode;
    private Long learningTime;
    private LocalDateTime createdAt;
    private Long solvingCount;


    public static LearningWithSolvingCountQueryDto from(Learning learning, long count){
        return new LearningWithSolvingCountQueryDto(learning.getMode(), learning.getLearningTime(), learning.getCreatedAt(), count);
    }
}
