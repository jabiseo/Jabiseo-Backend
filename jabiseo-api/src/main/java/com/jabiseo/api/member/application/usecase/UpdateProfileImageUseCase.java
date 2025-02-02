package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.request.UpdateProfileImageRequest;
import com.jabiseo.api.member.dto.response.UpdateProfileImageResponse;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.infra.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProfileImageUseCase {

    private final MemberService memberService;
    private final S3Uploader s3Uploader;
    private static final String PROFILE_IMAGE_PATH = "profile/";

    public UpdateProfileImageResponse execute(Long memberId, UpdateProfileImageRequest request) {
        Member member = memberService.getById(memberId);
        String profileUrl = s3Uploader.upload(request.image(), PROFILE_IMAGE_PATH);
        memberService.updateProfileImage(member, profileUrl);
        return UpdateProfileImageResponse.of(member);
    }

}
