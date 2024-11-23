package com.jabiseo.api.problem.dto.response;

import java.util.List;

public record ChoiceResponse(
        String choice
) {
    public static ChoiceResponse from(String choice) {
        return new ChoiceResponse(choice);
    }

    public static List<ChoiceResponse> fromChoices(List<String> choices) {
        return choices.stream()
                .map(ChoiceResponse::from)
                .toList();
    }
}
