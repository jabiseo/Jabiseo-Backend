package com.jabiseo.infra.opensearch;

import com.jabiseo.domain.analysis.exception.AnalysisBusinessException;
import com.jabiseo.domain.analysis.exception.AnalysisErrorCode;
import com.jabiseo.infra.opensearch.redis.SimilarProblemIdCache;
import com.jabiseo.infra.opensearch.redis.SimilarProblemIdCacheRepository;
import com.jabiseo.infra.opensearch.helper.OpenSearchHelper;
import com.jabiseo.domain.problem.service.SimilarProblemsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OpenSearchSimilarProblemsProvider implements SimilarProblemsProvider {

    private final OpenSearchHelper openSearchHelper;
    private final SimilarProblemIdCacheRepository similarProblemIdCacheRepository;

    public List<Long> findSimilarProblemIds(Long problemId, Long certificateId, int similarProblemsCount) {
        // 캐시에 저장된 유사 문제 ID가 있으면 반환, 없으면 opensearch에서 검색 후 캐시에 저장
        return similarProblemIdCacheRepository.findById(problemId)
                .orElseGet(() -> fetchAndCacheSimilarProblems(problemId, certificateId, similarProblemsCount))
                .getSimilarProblemIds();
    }

    private SimilarProblemIdCache fetchAndCacheSimilarProblems(Long problemId, Long certificateId, int similarProblemsCount) {
        // 자격증에 해당하는 인덱스 이름
        CertificateIndexInfo certificateIndexInfo = CertificateIndexInfo.findByCertificateId(certificateId);
        String indexName = certificateIndexInfo.getProblemIndexName();
        String vectorName = certificateIndexInfo.getProblemVectorName();

        List<Float> problemVector = fetchVector(problemId, indexName, vectorName);
        // N+1개의 유사 문제 ID를 가져온 후 첫 번째는 자기 자신이므로 제외
        List<Long> similarProblemIds =
                openSearchHelper.searchSimilarIds(problemVector, indexName, vectorName, similarProblemsCount + 1)
                        .subList(1, similarProblemsCount + 1);

        // 캐시에 저장 후 반환
        SimilarProblemIdCache cache = new SimilarProblemIdCache(problemId, similarProblemIds);
        return similarProblemIdCacheRepository.save(cache);
    }

    private List<Float> fetchVector(Long problemId, String indexName, String vectorName) {
        try {
            return openSearchHelper.fetchVector(problemId, indexName, vectorName);
        } catch (Exception e) {
            throw new AnalysisBusinessException(AnalysisErrorCode.CANNOT_FIND_VECTOR);
        }
    }

}
