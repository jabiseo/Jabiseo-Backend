package com.jabiseo.api.plan.application.usecase;

import com.jabiseo.api.plan.dto.calender.PlanCalenderSearchResponse;
import com.jabiseo.api.plan.dto.calender.PlanProgressDateResponse;
import com.jabiseo.api.plan.dto.calender.PlanProgressResponse;
import com.jabiseo.domain.common.exception.BusinessException;
import com.jabiseo.domain.common.exception.CommonErrorCode;
import com.jabiseo.domain.plan.domain.*;
import com.jabiseo.domain.plan.service.PlanProgressService;
import com.jabiseo.domain.plan.service.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SearchPlanCalenderUseCase {

    private final PlanProgressService planProgressService;
    private final PlanService planService;
    private final WeeklyDefineStrategy weeklyDefineStrategy;

    public PlanCalenderSearchResponse execute(Long memberId, Long planId, int year, int month) {
        Plan plan = planService.getByIdWithMember(planId);
        plan.checkOwner(memberId);

        List<PlanProgress> progressList = planProgressService.findByYearMonth(plan, year, month);

        List<WeekPeriod> periodPerWeekList = weeklyDefineStrategy.getPeriodPerWeek(year, month);
        List<PlanProgressDateResponse> dailyList = dailyProgressCovert(progressList);
        List<PlanProgressDateResponse> weeklyList = weeklyProgressCovert(progressList, periodPerWeekList);

        return new PlanCalenderSearchResponse(year, month, dailyList, weeklyList);
    }

    private List<PlanProgressDateResponse> weeklyProgressCovert(List<PlanProgress> progressList, List<WeekPeriod> weekPeriods) {
        Map<LocalDate, List<PlanProgress>> map = progressList.stream().filter(planProgress -> planProgress.getGoalType().equals(GoalType.WEEKLY))
                .collect(Collectors.groupingBy(PlanProgress::getProgressDate, Collectors.toList()));

        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<PlanProgressResponse> list = entry.getValue()
                            .stream()
                            .map((v) -> new PlanProgressResponse(v.getActivityType(), v.getCompletedValue(), v.getTargetValue()))
                            .toList();
                    return PlanProgressDateResponse.ofWeek(findIndexWeek(weekPeriods, entry.getKey()), list);
                }).toList();
    }

    private static int findIndexWeek(List<WeekPeriod> weekPeriods, LocalDate date) {
        return IntStream.range(1, weekPeriods.size()+1)
                .filter(i -> {
                    WeekPeriod period = weekPeriods.get(i-1);
                    return !date.isBefore(period.getStart()) && !date.isAfter(period.getEnd());
                })
                .findFirst()
                .orElseThrow(()-> new BusinessException(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }

    private List<PlanProgressDateResponse> dailyProgressCovert(List<PlanProgress> progressList) {
        Map<LocalDate, List<PlanProgress>> map = progressList.stream().filter(planProgress -> planProgress.getGoalType().equals(GoalType.DAILY))
                .collect(Collectors.groupingBy(PlanProgress::getProgressDate, Collectors.toList()));

        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<PlanProgressResponse> list = entry.getValue()
                            .stream()
                            .map((v) -> new PlanProgressResponse(v.getActivityType(), v.getCompletedValue(), v.getTargetValue()))
                            .toList();
                    return PlanProgressDateResponse.ofDay(entry.getKey(), list);
                }).toList();
    }


}
