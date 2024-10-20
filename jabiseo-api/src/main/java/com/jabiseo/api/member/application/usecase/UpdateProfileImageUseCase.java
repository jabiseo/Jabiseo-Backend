package com.jabiseo.api.member.application.usecase;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.api.member.dto.UpdateProfileImageRequest;
import com.jabiseo.api.member.dto.UpdateProfileImageResponse;
import com.jabiseo.infra.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProfileImageUseCase {

    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    private static final String PROFILE_IMAGE_PATH = "profile/";

    public UpdateProfileImageResponse execute(Long memberId, UpdateProfileImageRequest request) {
        Member member = memberRepository.getReferenceById(memberId);
        String profileUrl = s3Uploader.upload(request.image(), PROFILE_IMAGE_PATH);
        member.updateProfileImage(profileUrl);
        return UpdateProfileImageResponse.of(member);
    }

}
