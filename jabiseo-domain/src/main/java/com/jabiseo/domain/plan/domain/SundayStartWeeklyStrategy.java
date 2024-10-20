package com.jabiseo.domain.plan.domain;

import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Component
public class SundayStartWeeklyStrategy implements WeeklyDefineStrategy{

    @Override
    public List<WeekPeriod> getPeriodPerWeek(int year, int month) {
        List<WeekPeriod> periods = new ArrayList<>();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStartDate = yearMonth.atDay(1);
        LocalDate monthEndDate = yearMonth.atEndOfMonth();

        LocalDate startBoundary = monthStartDate.with(DayOfWeek.SUNDAY);

        // 월의 첫 날이 일요일 이후일 경우 그 전 주의 일요일로 이동
        if (monthStartDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            startBoundary = startBoundary.minusWeeks(1);
        }


        // 마지막 날이 속한 주의 토요일로 이동
        LocalDate endBoundary = monthEndDate.with(DayOfWeek.SATURDAY);


        // 월의 마지막 날이 일요일인 경우 그 다음 주의 토요일로 이동
        if (monthEndDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            endBoundary = endBoundary.plusWeeks(1);
        }

        // 각각의 Period 를 계산.
        LocalDate weekStart = startBoundary;
        while (!weekStart.isAfter(endBoundary)) {
            LocalDate weekEnd = weekStart.plusDays(6);

            if (weekEnd.isAfter(monthEndDate)) {
                weekEnd = monthEndDate;
            }

            periods.add(new WeekPeriod(weekStart, weekEnd));

            weekStart = weekStart.plusWeeks(1);
        }

        return periods;
    }

    @Override
    public WeekPeriod getWeekPeriod(LocalDate date) {
        LocalDate start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate end = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return new WeekPeriod(start, end);
    }
}
