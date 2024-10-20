package com.jabiseo.domain.plan.domain;

import com.jabiseo.domain.plan.domain.SundayStartWeeklyStrategy;
import com.jabiseo.domain.plan.domain.WeekPeriod;
import com.jabiseo.domain.plan.domain.WeeklyDefineStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("일요일시작 주간 정의 클래스 테스트")
class SundayStartWeeklyStrategyTest {

    WeeklyDefineStrategy weeklyDefineStrategy;

    @BeforeEach
    void setup() {
        weeklyDefineStrategy = new SundayStartWeeklyStrategy();
    }


    @ParameterizedTest
    @DisplayName("입력 날짜를 기준으로 주간 정의의 시작, 종료 값을 리턴한다")
    @ValueSource(strings = {"2024-09-01", "2024-09-09", "2024-09-17", "2024-09-25", "2024-10-03", "2024-10-11", "2024-10-12"})
    void getWeekPeriod(LocalDate now) throws Exception {
        //given
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        // getValue는 월 1 ~ 일 7이다. % 7 연산을 해 일요일인 경우 그 날 - 0 이 성립하도록 start 테스트 코드 구성
        LocalDate startOfWeek = now.minusDays(dayOfWeek.getValue() % 7);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        //when
        WeekPeriod currentWeekPeriod = weeklyDefineStrategy.getWeekPeriod(now);

        //then
        assertThat(currentWeekPeriod.getStart()).isEqualTo(startOfWeek);
        assertThat(currentWeekPeriod.getEnd()).isEqualTo(endOfWeek);
    }

}
