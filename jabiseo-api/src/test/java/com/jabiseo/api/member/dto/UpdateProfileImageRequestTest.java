package com.jabiseo.api.member.dto;

import com.jabiseo.api.member.dto.request.UpdateProfileImageRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;


import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("프로필 image 변경 DTO 테스트")
class UpdateProfileImageRequestTest {

    private final Validator validator;

    public UpdateProfileImageRequestTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("정확한 값을 넣었을때 성공한다")
    public void whenImageIsValid_thenNoConstraintViolations() {
        MockMultipartFile validFile = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1024]);
        UpdateProfileImageRequest request = new UpdateProfileImageRequest(validFile);

        Set<ConstraintViolation<UpdateProfileImageRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("null 값이 들어오면 예외를 반환한다")
    public void whenImageIsNull_thenShouldDetectConstraintViolations() {
        UpdateProfileImageRequest request = new UpdateProfileImageRequest(null);

        Set<ConstraintViolation<UpdateProfileImageRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("빈 파일 값이 들어오면 예외를 반환한다")
    public void whenImageIsEmpty_thenShouldDetectConstraintViolations() {
        MockMultipartFile emptyFile = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);
        UpdateProfileImageRequest request = new UpdateProfileImageRequest(emptyFile);

        Set<ConstraintViolation<UpdateProfileImageRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("큰 이미지 파일이 들어오면 예외를 반환한다")
    public void whenImageIsTooLarge_thenShouldDetectConstraintViolations() {
        byte[] largeFileContent = new byte[6 * 1024 * 1024]; // 6MB
        MockMultipartFile largeFile = new MockMultipartFile("image", "largeImage.jpg", "image/jpeg", largeFileContent);
        UpdateProfileImageRequest request = new UpdateProfileImageRequest(largeFile);

        Set<ConstraintViolation<UpdateProfileImageRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("허용되지 않은 확장자 형태가 들어오면 예외를 반환한다")
    public void whenImageHasInvalidExtension_thenShouldDetectConstraintViolations() {
        MockMultipartFile invalidFile = new MockMultipartFile("image", "image.txt", "text/plain", new byte[1024]);
        UpdateProfileImageRequest request = new UpdateProfileImageRequest(invalidFile);

        Set<ConstraintViolation<UpdateProfileImageRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

}
