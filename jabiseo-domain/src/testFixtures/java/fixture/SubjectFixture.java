package fixture;

import com.jabiseo.domain.certificate.domain.Certificate;
import com.jabiseo.domain.certificate.domain.Subject;
import org.springframework.test.util.ReflectionTestUtils;

public class SubjectFixture {

    public static Subject createSubject(Long subjectId, Certificate certificate) {
        Subject subject = Subject.of("subject name", 1, certificate);
        ReflectionTestUtils.setField(subject, "id", subjectId);
        return subject;
    }

    public static Subject createSubject(Certificate certificate) {
        return Subject.of("subject name", 1, certificate);
    }

}
