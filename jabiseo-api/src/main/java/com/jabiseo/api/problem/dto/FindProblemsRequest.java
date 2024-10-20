package com.jabiseo.api.problem.dto;

import java.util.List;

public record FindProblemsRequest(
        List<Long> problemIds
) {
}
