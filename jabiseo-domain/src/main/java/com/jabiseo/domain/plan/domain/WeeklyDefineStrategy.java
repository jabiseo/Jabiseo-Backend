package com.jabiseo.domain.plan.domain;

import java.time.LocalDate;
import java.util.List;

public interface WeeklyDefineStrategy {

    List<WeekPeriod> getPeriodPerWeek(int year, int month);

    WeekPeriod getWeekPeriod(LocalDate date);
}
