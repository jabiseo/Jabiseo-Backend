package com.jabiseo.api.learning.controller;

import com.jabiseo.api.learning.application.usecase.CreateLearningUseCase;
import com.jabiseo.api.config.auth.AuthMember;
import com.jabiseo.api.config.auth.AuthenticatedMember;
import com.jabiseo.api.learning.dto.CreateLearningRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learning")
public class LearningController {

    private final CreateLearningUseCase createLearningUseCase;

    @PostMapping
    public ResponseEntity<Void> createLearning(
            @AuthenticatedMember AuthMember member,
            @RequestBody @Valid CreateLearningRequest request
    ) {
        Long learningId = createLearningUseCase.execute(member.getMemberId(), request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(learningId)
                .toUri();

        return ResponseEntity.created(location).build();
    }
}
