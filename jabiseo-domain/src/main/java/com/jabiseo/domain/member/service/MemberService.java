package com.jabiseo.domain.member.service;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.MemberRepository;
import com.jabiseo.domain.member.domain.OauthMemberInfo;
import com.jabiseo.domain.member.exception.MemberBusinessException;
import com.jabiseo.domain.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberFactory memberFactory;

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getByIdWithCertificate(Long memberId) {
        return memberRepository.findByIdWithCertificate(memberId)
                .orElseThrow(() -> new MemberBusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getByOauthIdAndOauthServerOrCreateMember(OauthMemberInfo oauthMemberInfo) {
        return memberRepository.findByOauthIdAndOauthServer(oauthMemberInfo.getOauthId(), oauthMemberInfo.getOauthServer())
                .orElseGet(() -> {
                    Member newMember = memberFactory.createNew(oauthMemberInfo);
                    return memberRepository.save(newMember);
                });
    }

    @Transactional
    public void updateCurrentCertificate(Long memberId, Certificate certificate) {
        Member member = getById(memberId);
        member.updateCurrentCertificate(certificate);
    }

    @Transactional
    public Member updateNickname(Long memberId, String nickname) {
        Member member = getById(memberId);
        member.updateNickname(nickname);
        return member;
    }

    @Transactional
    public Member updateProfileImage(Long memberId, String profileUrl) {
        Member member = getById(memberId);
        member.updateProfileImage(profileUrl);
        return member;
    }
}
