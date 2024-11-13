package com.jabiseo.domain.auth.domain;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface AuthRepository {

    void saveAuth(Auth auth);
    Optional<Auth> getSavedAuth(Auth authKey);
    void deleteAuth(String authKey);
}
