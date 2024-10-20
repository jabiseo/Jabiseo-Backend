package fixture;

import com.jabiseo.domain.member.domain.Member;
import com.jabiseo.domain.member.domain.OauthServer;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberFixture {

    public static Member createMember(Long memberId) {
        Member member = Member.of("email", "name",
                "oauth2Id", OauthServer.KAKAO, "profileImage");
        ReflectionTestUtils.setField(member, "id", memberId);
        return member;
    }

    public static Member createMember(){
        return Member.of("email", "name",
                "oauth2Id", OauthServer.KAKAO, "profileImage");
    }


}
