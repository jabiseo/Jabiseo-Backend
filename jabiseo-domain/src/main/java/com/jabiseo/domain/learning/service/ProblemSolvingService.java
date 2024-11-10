package com.jabiseo.domain.learning.service;

import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.learning.domain.ProblemSolvingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProblemSolvingService {

    private final ProblemSolvingRepository problemSolvingRepository;

    public void saveAll(List<ProblemSolving> problemSolvings) {
        problemSolvingRepository.saveAll(problemSolvings);
    }

}
