package com.jabiseo.api.certificate.controller;

import com.jabiseo.api.certificate.dto.FindCertificateListResponse;
import com.jabiseo.api.certificate.dto.FindCertificateDetailResponse;
import com.jabiseo.api.certificate.application.usecase.FindCertificateDetailUseCase;
import com.jabiseo.api.certificate.application.usecase.FindCertificateListUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certificates")
public class CertificateController {

    private final FindCertificateListUseCase findCertificateListUseCase;

    private final FindCertificateDetailUseCase findCertificateDetailUseCase;

    @GetMapping
    public ResponseEntity<List<FindCertificateListResponse>> findCertificates() {
        List<FindCertificateListResponse> result = findCertificateListUseCase.execute();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{certificate-id}")
    public ResponseEntity<FindCertificateDetailResponse> findCertificate(
            @PathVariable(name = "certificate-id") Long certificateId
    ) {
        FindCertificateDetailResponse result = findCertificateDetailUseCase.execute(certificateId);
        return ResponseEntity.ok(result);
    }
}
