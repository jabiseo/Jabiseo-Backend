package com.jabiseo.infra.opensearch;

import com.jabiseo.infra.opensearch.helper.OpenSearchHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@DisplayName("OpenSearchProblemSearchProvider 테스트")
@ExtendWith(MockitoExtension.class)
class OpenSearchProblemSearchProviderTest {

    @InjectMocks
    OpenSearchProblemSearchProvider sut;

    @Mock
    OpenSearchHelper openSearchHelper;

    @ParameterizedTest
    @DisplayName("문제 검색 시 last score나 last id가 없으면 openSearchHelper.searchProblemInMultiField 메서드를 호출한다.")
    @CsvSource({"null, null", "null, 1", "1.0, null"})
    void givenNoLastScoreAndLastId_whenSearchProblem_thenCallOpenSearchHelperSearchProblemInMultiField(String lastScore, String lastId) {
        // given
        String query = "query";
        String indexName = "jeongbocheorigisa";
        int searchProblemCount = 10;

        // when
        sut.searchProblem(query, Objects.equals(lastScore, "null") ? null : 1.0, lastId.equals("null") ? null : 1L, 1L, searchProblemCount);

        // then
        verify(openSearchHelper).searchProblemInMultiField(eq(query), eq(indexName), any(), eq(searchProblemCount));
    }

    @Test
    @DisplayName("문제 검색 시 last score와 last id가 있으면 openSearchHelper.searchProblemInMultiFieldAfter 메서드를 호출한다.")
    void givenLastScoreAndLastId_whenSearchProblem_thenCallOpenSearchHelperSearchProblemInMultiFieldAfter() {
        // given
        String query = "query";
        Double lastScore = 1.0;
        Long lastId = 1L;
        String indexName = "jeongbocheorigisa";
        int searchProblemCount = 10;

        // when
        sut.searchProblem(query, lastScore, lastId, 1L, searchProblemCount);

        // then
        verify(openSearchHelper).searchProblemInMultiFieldAfter(eq(query), eq(indexName), any(), eq(lastScore.floatValue()), eq(lastId), eq(searchProblemCount));
    }

}
