package com.jabiseo.api.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageValidator implements ConstraintValidator<ImageValid, MultipartFile> {

    private static final List<String> ALLOW_EXTENSION_LIST = List.of("jpg", "jpeg", "png", "avif", "webp");
    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            return false;
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());
        if (!ALLOW_EXTENSION_LIST.contains(fileExtension)) {
            return false;
        }

        return true;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
