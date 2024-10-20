package com.jabiseo.infra.opensearch;

import com.jabiseo.infra.opensearch.helper.OpenSearchHelper;
import com.jabiseo.infra.opensearch.helper.OpenSearchResultDto;
import com.jabiseo.domain.problem.dto.ProblemSearchDto;
import com.jabiseo.domain.problem.service.ProblemSearchProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenSearchProblemSearchProvider implements ProblemSearchProvider {

    private final OpenSearchHelper openSearchHelper;

    // 검색할 필드 목록, OpenSearch의 결과에서 해당 컬럼명으로 값을 가져와야 함
    private static final List<String> SEARCH_FIELDS = List.of("description", "choice1", "choice2", "choice3", "choice4");

    @Override
    public List<ProblemSearchDto> searchProblem(String query, Double lastScore, Long lastId, Long certificateId, int searchProblemCount) {
        // 자격증에 해당하는 인덱스 이름
        CertificateIndexInfo certificateIndexInfo = CertificateIndexInfo.findByCertificateId(certificateId);
        String indexName = certificateIndexInfo.getProblemIndexName();
        List<OpenSearchResultDto> dtos = fetchFromOpenSearch(query, lastScore, lastId, indexName, searchProblemCount);
        return dtos.stream()
                .map(dto -> ProblemSearchDto.of(Long.parseLong(dto.id()), dto.score()))
                .toList();
    }

    private List<OpenSearchResultDto> fetchFromOpenSearch(String query, Double lastScore, Long lastId, String indexName, int searchProblemCount) {
        if (lastScore == null || lastId == null) {
            return openSearchHelper.searchProblemInMultiField(query, indexName, SEARCH_FIELDS, searchProblemCount);
        }
        return openSearchHelper.searchProblemInMultiFieldAfter(query, indexName, SEARCH_FIELDS, lastScore.floatValue(), lastId, searchProblemCount);
    }

}
