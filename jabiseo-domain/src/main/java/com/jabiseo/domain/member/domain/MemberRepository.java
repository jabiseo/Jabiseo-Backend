package com.jabiseo.domain.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthIdAndOauthServer(String oauthId, OauthServer oauthServer);

    @Query("SELECT m FROM Member m JOIN FETCH m.currentCertificate WHERE m.id = :memberId")
    Optional<Member> findByIdWithCertificate(Long memberId);
}
