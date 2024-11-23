package com.jabiseo.domain.auth.repository;

import com.jabiseo.domain.auth.domain.Auth;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface AuthRepository {

    void saveAuth(Auth auth);
    Optional<Auth> getSavedAuth(Auth authKey);
    void deleteAuth(String authKey);
}
