package com.jabiseo.domain.plan.domain;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WeekPeriod {

    private LocalDate start;
    private LocalDate end;

    public WeekPeriod(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }
}
