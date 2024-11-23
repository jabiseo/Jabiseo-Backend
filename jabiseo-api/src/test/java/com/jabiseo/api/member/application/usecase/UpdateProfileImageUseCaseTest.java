package com.jabiseo.api.member.application.usecase;

import com.jabiseo.api.member.dto.request.UpdateProfileImageRequest;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.service.MemberService;
import com.jabiseo.infra.s3.S3Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static fixture.MemberFixture.createMember;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("profileImage 변경 usecase 테스트")
class UpdateProfileImageUseCaseTest {
    @InjectMocks
    UpdateProfileImageUseCase useCase;

    @Mock
    MemberService memberService;

    @Mock
    S3Uploader s3Uploader;

    UpdateProfileImageRequest request;

    @BeforeEach
    void setUp() {
        request = new UpdateProfileImageRequest(new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[1024]));
    }

    @Test
    @DisplayName("프로필 이미지 변경 시 s3업로더에 업로드 후 회원 정보를 업데이트 한다.")
    void whenUpdateProfileImageThenUpdateImageInfo(){
        //given
        Member member = createMember(1L);
        String newImageUrl = "new-image-url";
        given(memberService.getById(member.getId())).willReturn(member);
        given(s3Uploader.upload(request.image(), "profile/")).willReturn(newImageUrl);

        //when
        useCase.execute(member.getId(), request);

        //then
        verify(memberService).updateProfileImage(member, newImageUrl);
    }

}
