package com.jabiseo.domain.certificate.repository;

import com.jabiseo.domain.certificate.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long>{

    @Query(value = "SELECT c FROM Certificate c JOIN FETCH c.exams e WHERE c.id = :certificateId")
    Optional<Certificate> findByIdWithExams(Long certificateId);
}
