package com.jabiseo.api.certificate.application.usecase;

import com.jabiseo.api.certificate.dto.FindCertificateListResponse;
import com.jabiseo.domain.certificate.domain.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindCertificateListUseCase {

    private final CertificateRepository certificateRepository;

    public List<FindCertificateListResponse> execute() {
        return certificateRepository
                .findAll()
                .stream()
                .map(FindCertificateListResponse::from)
                .toList();
    }
}
