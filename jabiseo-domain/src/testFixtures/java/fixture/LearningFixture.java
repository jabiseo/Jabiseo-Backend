package fixture;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.LearningMode;
import com.jabiseo.domain.member.domain.Member;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class LearningFixture {

    public static Learning createLearning(Long id, Member member, Certificate certificate) {
        Learning learning = Learning.of(LearningMode.EXAM, 123L, member, certificate);
        ReflectionTestUtils.setField(learning, "id", id);
        return learning;
    }

    public static Learning createLearning(Long id, Certificate certificate) {
        Learning learning = Learning.of(LearningMode.EXAM, 123L, MemberFixture.createMember(), certificate);
        ReflectionTestUtils.setField(learning, "id", id);
        return learning;
    }

    public static Learning createLearning(Long id, Certificate certificate, LocalDateTime createdAt) {
        Learning learning = Learning.of(LearningMode.EXAM, 123L, MemberFixture.createMember(), certificate);
        ReflectionTestUtils.setField(learning, "id", id);
        ReflectionTestUtils.setField(learning, "createdAt", createdAt);
        return learning;
    }

    public static Learning createLearning(Long id, Certificate certificate, LearningMode mode) {
        Learning learning = Learning.of(mode, 123L, MemberFixture.createMember(), certificate);
        ReflectionTestUtils.setField(learning, "id", id);
        return learning;
    }

}
