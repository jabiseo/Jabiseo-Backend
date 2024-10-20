package fixture;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.Exam;
import com.jabiseo.domain.certificate.domain.Subject;
import com.jabiseo.domain.problem.domain.Problem;
import com.jabiseo.domain.problem.domain.ProblemInfo;
import org.springframework.test.util.ReflectionTestUtils;

import static fixture.CertificateFixture.createCertificate;
import static fixture.ProblemInfoFixture.createProblemInfo;
import static fixture.SubjectFixture.createSubject;

public class ProblemFixture {
    public static Problem createProblem(Long id, Certificate certificate, Exam exam, Subject subject) {
        Problem problem = Problem.of(
                "problem description",
                "choice1",
                "choice2",
                "choice3",
                "choice4",
                1,
                "problem theory",
                1,
                certificate,
                exam,
                subject,
                createProblemInfo(certificate.getId(), exam.getId(), subject.getId())
        );
        ReflectionTestUtils.setField(problem, "id", id);
        return problem;
    }

    public static Problem createProblem(Long id, Certificate certificate) {
        return createProblem(id, certificate, ExamFixture.createExam(5432L, certificate), createSubject(9876L, certificate));
    }


    public static Problem createProblem(Long id) {
        return createProblem(id, createCertificate(1234L));
    }

    public static Problem createProblemWithAnswer(Long id, int answer, Certificate certificate) {
        Problem problem = Problem.of(
                "problem description",
                "choice1",
                "choice2",
                "choice3",
                "choice4",
                answer,
                "problem theory",
                1,
                certificate,
                ExamFixture.createExam(5432L, certificate),
                createSubject(9876L, certificate),
                createProblemInfo(certificate.getId(), 5432L, 9876L)
        );
        ReflectionTestUtils.setField(problem, "id", id);
        return problem;
    }

    public static Problem createProblem(Certificate certificate, Exam exam, Subject subject) {
        return Problem.of(
                "problem description",
                "choice1",
                "choice2",
                "choice3",
                "choice4",
                1,
                "problem theory",
                1,
                certificate,
                exam,
                subject,
                createProblemInfo(certificate.getId(), exam.getId(), subject.getId())
        );
    }

    public static Problem createProblem(Certificate certificate, Exam exam, Subject subject, ProblemInfo problemInfo) {
        return Problem.of(
                "problem description",
                "choice1",
                "choice2",
                "choice3",
                "choice4",
                1,
                "problem theory",
                1,
                certificate,
                exam,
                subject,
                problemInfo
        );
    }
}
