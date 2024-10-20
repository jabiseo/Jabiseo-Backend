package com.jabiseo.infra.opensearch;

import com.jabiseo.domain.analysis.exception.AnalysisBusinessException;
import com.jabiseo.domain.analysis.exception.AnalysisErrorCode;
import com.jabiseo.infra.opensearch.OpenSearchSimilarProblemsProvider;
import com.jabiseo.infra.opensearch.helper.OpenSearchHelper;
import com.jabiseo.infra.opensearch.redis.SimilarProblemIdCache;
import com.jabiseo.infra.opensearch.redis.SimilarProblemIdCacheRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayName("유사 문제 기능 테스트")
@ExtendWith(MockitoExtension.class)
class OpenSearchSimilarProblemsProviderTest {

    @InjectMocks
    OpenSearchSimilarProblemsProvider sut;

    @Mock
    SimilarProblemIdCacheRepository similarProblemIdCacheRepository;

    @Mock
    OpenSearchHelper openSearchHelper;

    @Test
    @DisplayName("redis 캐시에 유사 문제 id가 저장되어 있는 경우 Opensearch와 통신하지 않는다.")
    void givenCachedProblemId_whenFindSimilarProblemIds_thenDontConnectWithOpenSearch() throws Exception {
        //given
        Long certificateId = 1L; //정보처리기사
        Long problemId = 2L;
        int similarProblemsSize = 3;
        given(similarProblemIdCacheRepository.findById(problemId))
                .willReturn(Optional.of(new SimilarProblemIdCache(problemId, List.of(2L, 3L, 4L))));

        //when
        sut.findSimilarProblemIds(problemId, certificateId, similarProblemsSize);

        //then
        verify(openSearchHelper, never()).fetchVector(any(), any(), any());
        verify(openSearchHelper, never()).searchSimilarIds(any(), any(), any(), anyInt());
    }

    @Test
    @DisplayName("redis 캐시에 유사 문제 id가 저장되어 있지 않는 경우 Opensearch와 통신하고 결과를 캐시에 저장하고 반환한다.")
    void givenErrorInOpenSearchConnection_whenFindSimilarProblemIds_thenThrowNetworkApiException() throws Exception {
        //given
        Long certificateId = 1L; //정보처리기사
        Long problemId = 2L;
        int similarProblemsSize = 3;

        List<Float> problemVector = List.of(0.1f, 0.2f, 0.3f);

        given(similarProblemIdCacheRepository.findById(problemId)).willReturn(Optional.empty());
        given(openSearchHelper.fetchVector(any(), any(), any())).willReturn(problemVector);
        given(openSearchHelper.searchSimilarIds(eq(problemVector), any(), any(), eq(similarProblemsSize + 1)))
                .willReturn(List.of(2L, 3L, 4L, 5L));
        given(similarProblemIdCacheRepository.save(any())).willReturn(new SimilarProblemIdCache(problemId, List.of(3L, 4L, 5L)));

        //when
        List<Long> similarProblemIds = sut.findSimilarProblemIds(problemId, certificateId, similarProblemsSize);

        //then
        assertThat(similarProblemIds).containsExactly(3L, 4L, 5L);
    }

    @Test
    @DisplayName("opensearch와 통신 중 벡터를 가져올 수 없으면 예외처리한다.")
    void givenErrorInOpenSearchConnection_whenFindSimilarProblemIds_thenThrowAnalysisBusinessException() throws Exception {
        //given
        Long certificateId = 1L; //정보처리기사
        Long problemId = 2L;
        int similarProblemsSize = 3;

        given(similarProblemIdCacheRepository.findById(problemId)).willReturn(Optional.empty());
        given(openSearchHelper.fetchVector(any(), any(), any())).willThrow(RuntimeException.class);

        //when & then
        assertThatThrownBy(() -> sut.findSimilarProblemIds(problemId, certificateId, similarProblemsSize))
                .isInstanceOf(AnalysisBusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", AnalysisErrorCode.CANNOT_FIND_VECTOR);
    }



}
