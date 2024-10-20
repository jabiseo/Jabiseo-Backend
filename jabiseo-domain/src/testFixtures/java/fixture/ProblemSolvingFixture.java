package fixture;

import com.jabiseo.domain.learning.domain.Learning;
import com.jabiseo.domain.learning.domain.ProblemSolving;
import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.problem.domain.Problem;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static fixture.CertificateFixture.createCertificate;
import static fixture.LearningFixture.createLearning;
import static fixture.MemberFixture.createMember;
import static fixture.ProblemFixture.createProblem;

public class ProblemSolvingFixture {

    public static ProblemSolving createProblemSolving(Long id, Member member, Problem problem) {
        ProblemSolving problemSolving = ProblemSolving.of(
                member,
                problem,
                createLearning(12345L, createCertificate(), LocalDateTime.now()),
                4,
                true
        );
        ReflectionTestUtils.setField(problemSolving, "id", id);
        return problemSolving;
    }

    public static ProblemSolving createProblemSolving(Long id, Member member, Problem problem, Learning learning, boolean isCorrect) {
        ProblemSolving problemSolving = ProblemSolving.of(
                member,
                problem,
                learning,
                3,
                isCorrect
        );
        ReflectionTestUtils.setField(problemSolving, "id", id);
        return problemSolving;
    }

    public static ProblemSolving createProblemSolving(Long id, Learning learning, boolean isCorrect) {
        ProblemSolving problemSolving = ProblemSolving.of(
                createMember(),
                createProblem(1234L),
                learning,
                3,
                isCorrect
        );
        ReflectionTestUtils.setField(problemSolving, "id", id);
        return problemSolving;
    }
}
