package fixture;

import com.jabiseo.domain.certificate.domain.Certificate;
import org.springframework.test.util.ReflectionTestUtils;

public class CertificateFixture {

    public static Certificate createCertificate(Long certificateId) {
        Certificate certificate = Certificate.of("certificate name");
        ReflectionTestUtils.setField(certificate, "id", certificateId);
        return certificate;
    }

    public static Certificate createCertificate() {
        return Certificate.of("certificate name");
    }

}
