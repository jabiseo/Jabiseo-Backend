package fixture;

import com.jabiseo.domain.plan.domain.ActivityType;
import com.jabiseo.domain.plan.domain.GoalType;
import com.jabiseo.domain.plan.domain.PlanProgress;

import java.time.LocalDate;

public class PlanProgressFixture {

    public static PlanProgress createPlanProgress(LocalDate date, GoalType goalType, ActivityType activityType) {
        PlanProgress planProgress = PlanProgress.builder()
                .progressDate(date)
                .targetValue(10)
                .goalType(goalType)
                .activityType(activityType)
                .completedValue(0L)
                .plan(PlanFixture.createPlan(MemberFixture.createMember(), 1L))
                .build();

        return planProgress;
    }

}
