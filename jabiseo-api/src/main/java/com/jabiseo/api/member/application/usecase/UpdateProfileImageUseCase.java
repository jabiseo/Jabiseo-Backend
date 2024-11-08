package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.UpdateProfileImageRequest;
import com.jabiseo.api.member.dto.UpdateProfileImageResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.infra.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UpdateProfileImageUseCase {

    private final MemberService memberService;
    private final S3Uploader s3Uploader;
    private static final String PROFILE_IMAGE_PATH = "profile/";

    public UpdateProfileImageResponse execute(Long memberId, UpdateProfileImageRequest request) {
        String profileUrl = s3Uploader.upload(request.image(), PROFILE_IMAGE_PATH);
        Member member = memberService.updateProfileImage(memberId, profileUrl);
        return UpdateProfileImageResponse.of(member);
    }

}
