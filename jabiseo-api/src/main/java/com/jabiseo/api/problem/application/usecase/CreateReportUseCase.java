package com.jabiseo.api.problem.application.usecase;

import com.jabiseo.api.problem.dto.CreateReportRequest;
import org.springframework.stereotype.Service;

@Service
public class CreateReportUseCase {

    public String execute(CreateReportRequest request) {
        return "reportId";
    }
}
