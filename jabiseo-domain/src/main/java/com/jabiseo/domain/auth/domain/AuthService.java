package com.jabiseo.domain.auth.domain;

import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import com.jabiseo.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;

    public void login(Auth auth) {
        authRepository.saveAuth(auth);
    }

    public void reissue(Auth auth) {
        Auth savedAuth = authRepository.getSavedAuth(auth)
                .orElseThrow(() -> new AuthenticationBusinessException(AuthenticationErrorCode.REQUIRE_LOGIN));

        savedAuth.checkAuthToken(auth.getTokenValue());
    }


    public void logout(Auth auth) {
        authRepository.deleteAuth(auth.getAuthKey());
    }

}
