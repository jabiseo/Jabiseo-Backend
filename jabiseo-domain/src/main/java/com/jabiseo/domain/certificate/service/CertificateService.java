package com.jabiseo.domain.certificate.service;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.repository.CertificateRepository;
import com.jabiseo.domain.certificate.exception.CertificateBusinessException;
import com.jabiseo.domain.certificate.exception.CertificateErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateService {

    private final CertificateRepository certificateRepository;

    public Certificate getById(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new CertificateBusinessException(CertificateErrorCode.CERTIFICATE_NOT_FOUND));
    }

    public Certificate getByIdWithExams(Long certificateId) {
        return certificateRepository.findByIdWithExams(certificateId)
                .orElseThrow(() -> new CertificateBusinessException(CertificateErrorCode.CERTIFICATE_NOT_FOUND));
    }

    public void validateExamIdAndSubjectIds(Certificate certificate, Long examId, List<Long> subjectIds) {
        certificate.validateExamIdAndSubjectIds(examId, subjectIds);
    }

    public void validateExamIdAndSubjectIds(Long certificateId, Long examId, List<Long> subjectIds) {
        Certificate certificate = getByIdWithExams(certificateId);
        certificate.validateExamIdAndSubjectIds(examId, subjectIds);
    }

    public List<Certificate> findAll() {
        return certificateRepository.findAll();
    }
}
