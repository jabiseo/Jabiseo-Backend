package com.jabiseo.domain.learning.service;

import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.learning.domain.LearningRepository;
import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.learning.dto.TodayLearningDto;
import com.jabiseo.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningService {

    private final LearningRepository learningRepository;

    public TodayLearningDto findTodayLearning(Member member) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        // 오늘 학습한 내용 (Learning, ProblemSolving) 조회
        List<Learning> learnings = learningRepository.findByMemberAndCreatedAtBetweenWithProblemSolvings(member, startOfDay, endOfDay);
        // Learning Mode 별 Learning 횟수
        Map<LearningMode, Integer> learningCountMap = countLearningByMode(learnings);

        // 오늘 푼 문제 조회
        List<ProblemSolving> problemSolvings = learnings.stream()
                .map(Learning::getProblemSolvings)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        // Learning Mode 별 문제 푼 횟수
        Map<LearningMode, Integer> problemSolvingCountMap = countProblemSolvingByType(problemSolvings);
        // Learning Mode 별 정답 횟수
        Map<LearningMode, Integer> correctProblemSolvingCountMap = countCorrectProblemSolvingByMode(problemSolvings);

        // 정답률 = (정답 수 / 문제 푼 수) * 100
        int studyModeCorrectRate = calculateRate(
                correctProblemSolvingCountMap.getOrDefault(LearningMode.STUDY, 0),
                problemSolvingCountMap.getOrDefault(LearningMode.STUDY, 1)
        );
        int examModeCorrectRate = calculateRate(
                correctProblemSolvingCountMap.getOrDefault(LearningMode.EXAM, 0),
                problemSolvingCountMap.getOrDefault(LearningMode.EXAM, 1)
        );

        return TodayLearningDto.of(
                learningCountMap.getOrDefault(LearningMode.STUDY, 0),
                studyModeCorrectRate,
                learningCountMap.getOrDefault(LearningMode.EXAM, 0),
                examModeCorrectRate
        );
    }

    private Map<LearningMode, Integer> countLearningByMode(List<Learning> learnings) {
        return learnings.stream()
                .collect(Collectors.groupingBy(
                        Learning::getMode,
                        Collectors.summingInt(learning -> 1)
                ));
    }

    private Map<LearningMode, Integer> countProblemSolvingByType(List<ProblemSolving> problemSolvings) {
        return problemSolvings.stream()
                .collect(Collectors.groupingBy(
                        problemSolving -> problemSolving.getLearning().getMode(),
                        Collectors.summingInt(problemSolving -> 1)
                ));
    }

    private Map<LearningMode, Integer> countCorrectProblemSolvingByMode(List<ProblemSolving> problemSolvings) {
        return problemSolvings.stream()
                .filter(ProblemSolving::isCorrect)
                .collect(Collectors.groupingBy(
                        problemSolving -> problemSolving.getLearning().getMode(),
                        Collectors.summingInt(problemSolving -> 1)
                ));
    }

    private int calculateRate(Integer numerator, int denominator) {
        return (int) Math.round(((double) numerator / denominator * 100));
    }
}
