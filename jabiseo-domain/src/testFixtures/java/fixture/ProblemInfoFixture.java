package fixture;

import com.jabiseo.domain.problem.domain.ProblemInfo;
import org.springframework.test.util.ReflectionTestUtils;

public class ProblemInfoFixture {

    public static ProblemInfo createProblemInfo(Long certificateId, Long examId, Long subjectId) {
        return ProblemInfo.of(certificateId, "certificate name", examId, "exam description", 2021, 1, subjectId, "subject name", 1);
    }

    public static ProblemInfo createProblemInfo(Long id, Long certificateId, Long examId, Long subjectId) {
        ProblemInfo problemInfo = ProblemInfo.of(certificateId, "certificate name", examId, "exam description", 2021, 1, subjectId, "subject name", 1);
        ReflectionTestUtils.setField(problemInfo, "id", id);
        return problemInfo;
    }
}
