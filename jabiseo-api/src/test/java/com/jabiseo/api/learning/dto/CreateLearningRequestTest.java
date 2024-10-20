package com.jabiseo.api.learning.dto;

import com.jabiseo.api.learning.dto.CreateLearningRequest;
import com.jabiseo.api.learning.dto.ProblemResultRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateLearning 입력값 검증 테스트")
class CreateLearningRequestTest {

    private Validator validator;

    @BeforeEach
    void init() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @DisplayName("CreateLearning 요청시 learningTime에 정확한 값이 오지 않으면 예외를 반환한다.")
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L, 1000000001L})
    void givenCreateLearningRequestWithWrongLearningTime_WhenCreateLearningDto_ThenReturnError(long learningTime) {
        //given
        List<ProblemResultRequest> problemResultRequests = List.of(new ProblemResultRequest(1L, 1));
        CreateLearningRequest createLearningRequest = new CreateLearningRequest(learningTime, "EXAM", 1L, problemResultRequests);

        //when
        Set<ConstraintViolation<CreateLearningRequest>> violations = validator.validate(createLearningRequest);

        //then
        assertThat(violations).isNotEmpty();
    }

    @DisplayName("CreateLearning 요청시 learningMode에 정확한 값이 오지 않으면 예외를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "WRONG", "exam"})
    void givenCreateLearningRequestWithWrongLearningMode_WhenCreateLearningDto_ThenReturnError(String learningMode) {
        //given
        List<ProblemResultRequest> problemResultRequests = List.of(new ProblemResultRequest(1L, 1));
        CreateLearningRequest createLearningRequest = new CreateLearningRequest(100L, learningMode, 1L, problemResultRequests);

        //when
        Set<ConstraintViolation<CreateLearningRequest>> violations = validator.validate(createLearningRequest);

        //then
        assertThat(violations).isNotEmpty();
    }

    @DisplayName("CreateLearning 요청시 certificateId에 정확한 값이 오지 않으면 예외를 반환한다.")
    @Test
    void givenCreateLearningRequestWithWrongCertificateId_WhenCreateLearningDto_ThenReturnError() {
        //given
        Long certificateId = null;
        List<ProblemResultRequest> problemResultRequests = List.of(new ProblemResultRequest(1L, 1));
        CreateLearningRequest createLearningRequest = new CreateLearningRequest(100L, "EXAM", certificateId, problemResultRequests);

        //when
        Set<ConstraintViolation<CreateLearningRequest>> violations = validator.validate(createLearningRequest);

        //then
        assertThat(violations).isNotEmpty();
    }

    @DisplayName("CreateLearning 요청시 problems가 비어 있으면 예외를 반환한다.")
    @Test
    void givenCreateLearningRequestWithWrongProblems_WhenCreateLearningDto_ThenReturnError() {
        //given
        List<ProblemResultRequest> problemResultRequests = List.of();
        CreateLearningRequest createLearningRequest = new CreateLearningRequest(100L, "EXAM", 1L, problemResultRequests);

        //when
        Set<ConstraintViolation<CreateLearningRequest>> violations = validator.validate(createLearningRequest);

        //then
        assertThat(violations).isNotEmpty();
    }

    @DisplayName("ProblemResultRequest 생성시 problemId가 null이면 예외를 반환한다.")
    @Test
    void givenCreateLearningRequestWithNullProblemId_WhenCreateProblemResultRequest_ThenReturnError() {
        //given
        Long problemId = null;
        ProblemResultRequest problemResultRequest = new ProblemResultRequest(problemId, 1);

        //when
        Set<ConstraintViolation<ProblemResultRequest>> violations = validator.validate(problemResultRequest);

        //then
        assertThat(violations).isNotEmpty();
    }

    @DisplayName("ProblemResultRequest 생성시 choice에 정확한 값이 오지 않으면 예외를 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 5, 100})
    void givenCreateLearningRequestWithWrongChoice_WhenCreateProblemResultRequest_ThenReturnError(int choice) {
        //given
        ProblemResultRequest problemResultRequest = new ProblemResultRequest(1L, choice);

        //when
        Set<ConstraintViolation<ProblemResultRequest>> violations = validator.validate(problemResultRequest);

        //then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("정상적인 입력 요청 시 성공한다.")
    void givenValidRequest_whenCreateLearning_thenSuccess() {
        //given
        List<ProblemResultRequest> problemResultRequests = List.of(new ProblemResultRequest(1L, 1));
        CreateLearningRequest createLearningRequest = new CreateLearningRequest(100L, "EXAM", 1L, problemResultRequests);

        //when
        Set<ConstraintViolation<CreateLearningRequest>> violations = validator.validate(createLearningRequest);

        //then
        assertThat(violations).isEmpty();
    }

}
