package com.jabiseo.domain.member.repository;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.OauthServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByOauthIdAndOauthServer(String oauthId, OauthServer oauthServer);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.currentCertificate WHERE m.id = :memberId")
    Optional<Member> findByIdWithCertificate(Long memberId);

}
