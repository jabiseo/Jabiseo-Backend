package fixture;

import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.dto.ProblemWithBookmarkDetailQueryDto;

public class ProblemWithBookmarkDetailQueryDtoFixture {
    public static ProblemWithBookmarkDetailQueryDto createProblemWithBookmarkDetailQueryDto(Problem problem, boolean isBookmark) {
        return new ProblemWithBookmarkDetailQueryDto(
                problem.getId(),
                problem.getDescription(),
                problem.getChoice1(),
                problem.getChoice2(),
                problem.getChoice3(),
                problem.getChoice4(),
                problem.getAnswerNumber(),
                problem.getSolution(),
                isBookmark,
                problem.getExam().getId(),
                problem.getExam().getDescription(),
                problem.getSubject().getId(),
                problem.getSubject().getName(),
                problem.getSubject().getSequence()
        );
    }
}
