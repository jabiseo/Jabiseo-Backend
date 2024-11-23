package com.jabiseo.api.problem.dto.request;

import java.util.List;

public record FindProblemsRequest(
        List<Long> problemIds
) {
}
