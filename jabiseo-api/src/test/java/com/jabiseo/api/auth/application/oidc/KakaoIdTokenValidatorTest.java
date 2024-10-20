package com.jabiseo.api.auth.application.oidc;

import com.jabiseo.api.auth.application.oidc.KakaoIdTokenValidator;
import com.jabiseo.api.auth.application.oidc.property.KakaoOidcProperty;
import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import com.jabiseo.infra.cache.RedisCacheRepository;
import com.jabiseo.infra.client.oidc.KakaoKauthClient;
import com.jabiseo.infra.client.NetworkApiException;
import com.jabiseo.infra.client.oidc.OidcPublicKey;
import com.jabiseo.infra.client.oidc.OidcPublicKeyResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@DisplayName("카카오IdToken검증 테스트")
@ExtendWith(MockitoExtension.class)
class KakaoIdTokenValidatorTest {

    @InjectMocks
    KakaoIdTokenValidator validator;

    @Mock
    KakaoOidcProperty kakaoOidcProperty;


    @Mock
    KakaoKauthClient kakaoKauthClient;

    @Mock
    RedisCacheRepository redisCacheRepository;


    @BeforeEach
    void setUp() {
        kakaoOidcProperty = new KakaoOidcProperty("client id", "adminKey", "issuer");
    }

    @Test
    @DisplayName("카카오 oidc  public key 조회시 캐시에 데이터가 있다면 카카오API를 호출하지 않는다. ")
    void notCallApiAlreadySavedCache() {
        //given
        given(redisCacheRepository.getPublicKeys(any())).willReturn(List.of(mockPublicKey("kid1"), mockPublicKey("kid2")));

        //when
        validator.getOidcPublicKey("kid1");

        //then
        verify(kakaoKauthClient, times(0)).getPublicKeys();
    }

    @Test
    @DisplayName("카카오 oidc  public key 조회시 캐시에 데이터가 없다면 카카오API를 호출하고 저장한다")
    void callApiNotSavedCache() {
        //given
        List<OidcPublicKey> publicKeys = List.of(mockPublicKey("kid1"), mockPublicKey("kid2"));
        ResponseEntity<OidcPublicKeyResponse> entity = ResponseEntity.of(Optional.of(new OidcPublicKeyResponse(publicKeys)));
        given(redisCacheRepository.getPublicKeys(any())).willReturn(null);
        given(kakaoKauthClient.getPublicKeys()).willReturn(entity);

        //when
        validator.getOidcPublicKey("kid1");

        //then
        verify(redisCacheRepository, times(1)).savePublicKey(any(), any());
    }

    @Test
    @DisplayName("카카오 oidc public key조회시 kid에 맞는 key가 없다면 예외를 반환한다.")
    void notMatchKidThrownException() {
        //given
        List<OidcPublicKey> publicKeys = List.of(mockPublicKey("kid1"), mockPublicKey("kid2"));
        given(redisCacheRepository.getPublicKeys(any())).willReturn(publicKeys);
        String notMatchKid = "kidkid";

        //when
        assertThatThrownBy(() -> validator.getOidcPublicKey(notMatchKid))
                .isInstanceOf(AuthenticationBusinessException.class)
                .hasMessage(AuthenticationErrorCode.INVALID_ID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("카카오 oidc public key조회시 kid에 맞는 key가 있다면 해당 Public Key를 리턴한다.")
    void SuccessMatchKidReturnOidcPublicKey() {
        //given
        String matchKid = "kid0";
        OidcPublicKey matchPublicKey = mockPublicKey(matchKid);
        given(redisCacheRepository.getPublicKeys(any())).willReturn(List.of(mockPublicKey("kid1"),matchPublicKey));

        //when
        OidcPublicKey oidcPublicKey = validator.getOidcPublicKey(matchKid);

        //then
        Assertions.assertThat(oidcPublicKey).isEqualTo(matchPublicKey);
    }

    @Test
    @DisplayName("카카오 jwk 획득 api 호출 실패시 에러를 반환한다")
    void getJwkKakaoApiCallingFailThrownException(){
        //given
        given(redisCacheRepository.getPublicKeys(any())).willReturn(null);
        given(kakaoKauthClient.getPublicKeys()).willThrow(NetworkApiException.class);

        //when then
        assertThatThrownBy(() -> validator.getOidcPublicKey("key"))
                .isInstanceOf(AuthenticationBusinessException.class)
                .hasMessage(AuthenticationErrorCode.GET_JWK_FAIL.getMessage());

    }

    private OidcPublicKey mockPublicKey(String kid) {
        return new OidcPublicKey(kid, "a", "u", "n", "e",".");
    }
}
