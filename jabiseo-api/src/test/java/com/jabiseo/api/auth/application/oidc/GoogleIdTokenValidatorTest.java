package com.jabiseo.api.auth.application.oidc;

import com.jabiseo.api.auth.application.oidc.GoogleIdTokenValidator;
import com.jabiseo.api.auth.application.oidc.property.GoogleOidcProperty;
import com.jabiseo.domain.auth.exception.AuthenticationBusinessException;
import com.jabiseo.domain.auth.exception.AuthenticationErrorCode;
import com.jabiseo.infra.cache.RedisCacheRepository;
import com.jabiseo.infra.client.NetworkApiException;
import com.jabiseo.infra.client.oidc.GoogleAccountsClient;
import com.jabiseo.infra.client.oidc.GoogleOidcClient;
import com.jabiseo.infra.client.oidc.GoogleOpenIdConfiguration;
import com.jabiseo.infra.client.oidc.OidcPublicKey;
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
import static org.mockito.Mockito.*;

@DisplayName("구글 IdToken 검증 테스트")
@ExtendWith(MockitoExtension.class)
class GoogleIdTokenValidatorTest {

    @InjectMocks
    GoogleIdTokenValidator validator;

    @Mock
    GoogleOidcProperty googleOidcProperty;

    @Mock
    RedisCacheRepository redisCacheRepository;

    @Mock
    GoogleAccountsClient googleAccountsClient;

    @Mock
    GoogleOidcClient googleOidcClient;

    GoogleOpenIdConfiguration configuration = mockGoogleOpenIdConfiguration();

    @BeforeEach
    void setUp() {
        googleOidcProperty = new GoogleOidcProperty("client_id", "issuer");
    }

    @Test
    @DisplayName("구글 jwk 조회시 캐시에 데이터가 있다면 Google API를 호출하지 않는다.")
    void notCallApiAlreadySavedCache() {
        //given
        given(redisCacheRepository.getPublicKeys(any(String.class))).willReturn(List.of(mockPublicKey("kid1"), mockPublicKey("kid2")));

        //when
        validator.getOidcPublicKey("kid1");

        //then
        verify(googleOidcClient, never()).getPublicKeys("urls");
        verify(googleAccountsClient, never()).getOidcConfiguration();
    }

    @Test
    @DisplayName("구글 jwk 조회시 캐시에 데이터가 없다면 캐시 데이터를 호출하고 구글API호출 후 캐시에 저장한다.")
    void callApiNotSavedCallGoogleAPIAndSavedCache(){
        //given
        List<OidcPublicKey> publicKeys = List.of(mockPublicKey("kid1"), mockPublicKey("kid2"));
        given(redisCacheRepository.getPublicKeys(any())).willReturn(null); // cache hit fail
        given(redisCacheRepository.getGoogleConfiguration(any())).willReturn(configuration); // cahce hit success
        given(googleOidcClient.getPublicKeys(configuration.getJwks_uri())).willReturn(publicKeys);

        //when
        validator.getOidcPublicKey("kid1");

        //then
        verify(googleOidcClient, times(1)).getPublicKeys(configuration.getJwks_uri());
        verify(redisCacheRepository, times(1)).savePublicKey("GOOGLE_OIDC_PUBLIC_KEY",publicKeys);
    }

    @Test
    @DisplayName("구글 jwk 조회시 jwk도 없고 openid정보도 없다면 둘 다 호출한다")
    void isNullJwKAndOpenIdCallingJwkAndOpenId(){
        //given
        ResponseEntity<GoogleOpenIdConfiguration> res = ResponseEntity.of(Optional.of(configuration));

        List<OidcPublicKey> publicKeys = List.of(mockPublicKey("kid1"), mockPublicKey("kid2"));
        given(redisCacheRepository.getPublicKeys(any())).willReturn(null); // cache hit fail
        given(redisCacheRepository.getGoogleConfiguration(any())).willReturn(null); // cahce hit success
        given(googleOidcClient.getPublicKeys(any())).willReturn(publicKeys);
        given(googleAccountsClient.getOidcConfiguration()).willReturn(res);

        //when
        validator.getOidcPublicKey("kid1");

        //then
        verify(googleAccountsClient, times(1)).getOidcConfiguration();
        verify(googleOidcClient, times(1)).getPublicKeys(any());
    }

    @Test
    @DisplayName("구글 jwk 조회시 kid 에 맞는 key가 없다면 예외를 반환한다.")
    void notMatchKidThrownException(){
        //given
        List<OidcPublicKey> publicKeys = List.of(mockPublicKey("kid1"), mockPublicKey("kid2"));
        given(redisCacheRepository.getPublicKeys(any())).willReturn(publicKeys);
        String otherKid = "kid";
        //when //then

        Assertions.assertThatThrownBy(()->validator.getOidcPublicKey(otherKid))
                .isInstanceOf(AuthenticationBusinessException.class)
                .hasMessage(AuthenticationErrorCode.INVALID_ID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("구글 jwk 획득 중 api 호출 실패시 예외를 반환한다")
    void getJwkKakaoApiCallingFailThrownException(){
        //given
        given(redisCacheRepository.getPublicKeys(any())).willReturn(null);
        given(redisCacheRepository.getGoogleConfiguration(any())).willReturn(configuration);
        given(googleOidcClient.getPublicKeys(configuration.getJwks_uri())).willThrow(NetworkApiException.class);

        //when //then
        assertThatThrownBy(() -> validator.getOidcPublicKey("key"))
                .isInstanceOf(AuthenticationBusinessException.class)
                .hasMessage(AuthenticationErrorCode.GET_JWK_FAIL.getMessage());
    }

    @Test
    @DisplayName("구글 jwk 획등 중 kid에 맞는 jwk가 있다면 반환한다")
    void successMatchKidReturnOidcPublicKey(){
        //given
        String matchKid = "kid";
        OidcPublicKey matchPublicKey = mockPublicKey(matchKid);
        List<OidcPublicKey> publicKeys = List.of(matchPublicKey, mockPublicKey("kid2"));

        given(redisCacheRepository.getPublicKeys(any())).willReturn(publicKeys);

        //when
        OidcPublicKey key = validator.getOidcPublicKey("kid");

        //then
        Assertions.assertThat(key).isEqualTo(matchPublicKey);
    }


    private GoogleOpenIdConfiguration mockGoogleOpenIdConfiguration() {
        return GoogleOpenIdConfiguration
                .builder()
                .jwks_uri("https..")
                .build();
    }


    private OidcPublicKey mockPublicKey(String kid) {
        return new OidcPublicKey(kid, "a", "u", "n", "e", ".");
    }
}
