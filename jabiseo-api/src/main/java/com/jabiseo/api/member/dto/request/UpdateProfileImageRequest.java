package com.jabiseo.api.member.dto.request;

import com.jabiseo.api.common.validator.ImageValid;
import org.springframework.web.multipart.MultipartFile;

public record UpdateProfileImageRequest(
        @ImageValid MultipartFile image
) {
}
