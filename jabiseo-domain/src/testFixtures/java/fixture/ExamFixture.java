package fixture;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.Exam;
import org.springframework.test.util.ReflectionTestUtils;

public class ExamFixture {

    public static Exam createExam(Long examId, Certificate certificate) {
        Exam exam =  Exam.of("exam description", 2000, 1, certificate);
        ReflectionTestUtils.setField(exam, "id", examId);
        return exam;
    }

    public static Exam createExam(Certificate certificate) {
        return Exam.of("exam description", 2000, 1, certificate);
    }

}
