package com.jabiseo.api.certificate.application.usecase;

import com.jabiseo.api.certificate.dto.FindCertificateListResponse;
import com.jabiseo.domain.certificate.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindCertificateListUseCase {

    private final CertificateService certificateService;

    public List<FindCertificateListResponse> execute() {
        return certificateService
                .findAll()
                .stream()
                .map(FindCertificateListResponse::from)
                .toList();
    }
}
