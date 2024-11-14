package com.jabiseo.domain.auth.domain;

import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class Auth {
    private static final String PREFIX = "AUTH";
    private String deviceId;
    private Long memberId;
    private String tokenValue;


    public String getAuthKey() {
        return PREFIX + "_" + memberId + "_" + deviceId;
    }

    public static Auth create(String deviceId, Long memberId, String tokenValue) {
        return new Auth(deviceId, memberId, tokenValue);
    }

    public void checkAuthToken(String token) {
        if (!tokenValue.equals(token)) {
            throw new AuthenticationBusinessException(AuthenticationErrorCode.NOT_MATCH_REFRESH);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Auth auth = (Auth) o;
        return Objects.equals(deviceId, auth.deviceId) &&
                Objects.equals(memberId, auth.memberId) &&
                Objects.equals(tokenValue, auth.tokenValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, memberId, tokenValue);
    }
}
