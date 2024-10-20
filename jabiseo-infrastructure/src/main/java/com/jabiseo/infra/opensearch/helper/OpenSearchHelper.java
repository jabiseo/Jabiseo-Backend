package com.jabiseo.infra.opensearch.helper;

import com.jabiseo.infra.client.NetworkApiErrorCode;
import com.jabiseo.infra.client.NetworkApiException;
import jakarta.json.JsonNumber;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.TermQuery;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class OpenSearchHelper {

    private static final int KNN_K = 100;

    private final OpenSearchClient openSearchClient;

    public List<Float> fetchVector(Long id, String indexName, String vectorName) {
        GetResponse<JsonData> getResponse = fetchFromOpenSearch(id, indexName, vectorName);
        return parseVectorFromGetResponse(getResponse, vectorName);
    }

    // 여러 벡터를 한 번에 가져오는 경우 mget을 사용해 한 번의 통신으로 가져온다.
    public Map<Long, List<Float>> fetchVectors(List<Long> ids, String indexName, String vectorName) {
        MgetResponse<JsonData> mgetResponse = fetchFromOpenSearch(ids, indexName, vectorName);
        return parseVectorsFromMgetResponse(vectorName, mgetResponse);
    }

    private GetResponse<JsonData> fetchFromOpenSearch(Long id, String indexName, String vectorName) {
        try {
            return openSearchClient.get(GetRequest.of(request -> request
                    .index(indexName)
                    .id(String.valueOf(id))
                    .sourceIncludes(vectorName)
            ), JsonData.class);
        } catch (IOException e) {
            throw new NetworkApiException(NetworkApiErrorCode.OPEN_SEARCH_API_FAIL);
        }
    }

    private MgetResponse<JsonData> fetchFromOpenSearch(List<Long> ids, String indexName, String vectorName) {
        try {
            return openSearchClient.mget(MgetRequest.of(request -> request
                    .index(indexName)
                    .ids(ids.stream().map(String::valueOf).toList())
                    .sourceIncludes(vectorName)
            ), JsonData.class);
        } catch (IOException e) {
            throw new NetworkApiException(NetworkApiErrorCode.OPEN_SEARCH_API_FAIL);
        }
    }

    private List<Float> parseVectorFromGetResponse(GetResponse<JsonData> getResponse, String vectorName) {
        if (!getResponse.found()) {
            throw new NetworkApiException(NetworkApiErrorCode.OPEN_SEARCH_API_FAIL);
        }
        return getResponse.source()
                .toJson()
                .asJsonObject()
                .get(vectorName)
                .asJsonArray()
                .stream()
                .map(JsonNumber.class::cast)
                .map(jsonNumber -> (float) jsonNumber.doubleValue())
                .toList();
    }

    private static Map<Long, List<Float>> parseVectorsFromMgetResponse(String vectorName, MgetResponse<JsonData> mgetResponse) {
        return mgetResponse.docs().stream()
                .peek(hit -> {
                    if (!hit.result().found()) {
                        throw new NetworkApiException(NetworkApiErrorCode.OPEN_SEARCH_API_FAIL);
                    }
                })
                .collect(Collectors.toMap(
                        hit -> Long.parseLong(hit.result().id()),
                        hit -> hit.result().source().toJson().asJsonObject().get(vectorName).asJsonArray().stream()
                                .map(JsonNumber.class::cast)
                                .map(jsonNumber -> (float) jsonNumber.doubleValue())
                                .toList()
                ));
    }

    public List<Long> searchSimilarIds(List<Float> vector, String indexName, String vectorName, int targetSize) {
        SearchRequest searchRequest = createKnnSearchRequestForIds(vector, indexName, vectorName, targetSize);
        SearchResponse<JsonData> searchResponse = executeSearch(searchRequest);
        return extractIds(searchResponse, targetSize);
    }

    public List<OpenSearchResultDto> searchSimilarItems(List<Float> vector, String indexName, String vectorName) {
        SearchRequest searchRequest = createKnnSearchRequest(vector, indexName, vectorName);
        SearchResponse<JsonData> searchResponse = executeSearch(searchRequest);
        return extractResult(searchResponse);
    }

    public List<OpenSearchResultDto> searchSimilarItems(List<Float> vector, String indexName, String vectorName, int targetSize) {
        SearchRequest searchRequest = createKnnSearchRequest(vector, indexName, vectorName, targetSize);
        SearchResponse<JsonData> searchResponse = executeSearch(searchRequest);
        return extractResult(searchResponse);
    }

    public List<Long> searchSimilarIdsWithFiltering(List<Float> vector, String indexName, String vectorName, int targetSize, String filterName, Long filterValue) {
        SearchRequest searchRequest = createKnnSearchRequestForIdsWithFiltering(vector, indexName, vectorName, targetSize, filterName, filterValue);
        SearchResponse<JsonData> searchResponse = executeSearch(searchRequest);
        return extractIds(searchResponse, targetSize);
    }

    public List<OpenSearchResultDto> searchProblemInMultiField(String query, String indexName, List<String> fields, int targetSize) {
        SearchRequest searchRequest = SearchRequest.of(s ->
                s.index(indexName)
                        .size(targetSize)
                        .source(source -> source
                                .filter(filter -> filter.includes("")))
                        .query(q -> q
                                .multiMatch(multiMatch -> multiMatch
                                        .query(query)
                                        .fields(fields)
                                )
                        )
        );
        SearchResponse<JsonData> searchResponse = executeSearch(searchRequest);
        return extractResult(searchResponse);
    }

    public List<OpenSearchResultDto> searchProblemInMultiFieldAfter(String query, String indexName, List<String> fields, Float lastScore, Long lastId, int targetSize) {
        SearchRequest searchRequest = SearchRequest.of(s ->
                        s.index(indexName)
                                .size(targetSize)
                                .searchAfter(String.valueOf(lastScore), String.valueOf(lastId))
                                .sort(sort -> sort.score(scoreSort -> scoreSort.order(SortOrder.Desc)))
                                .sort(sort -> sort.field(fieldSort -> fieldSort.field("_id").order(SortOrder.Asc)))
                                .source(source -> source
                                        .filter(filter -> filter.includes("")))
                                .query(q -> q
                                        .multiMatch(multiMatch -> multiMatch
                                                .query(query)
                                                .fields(fields)
                                        )
                                )
        );
        SearchResponse<JsonData> searchResponse = executeSearch(searchRequest);
        return extractResult(searchResponse);
    }

    private SearchRequest createKnnSearchRequestForIds(List<Float> vector, String indexName, String vectorName, int size) {
        // id만 필요한 경우, source 필드는 제외
        return SearchRequest.of(searchRequest ->
                searchRequest.index(indexName)
                        .size(size)
                        .source(source -> source
                                // id만 필요하므로 나머지 필드는 제외
                                .filter(filter -> filter.includes("")))
                        .query(query -> query
                                .knn(knn -> knn
                                        .field(vectorName)
                                        .vector(convertToArray(vector))
                                        .k(KNN_K)
                                )
                        )
        );
    }

    private SearchRequest createKnnSearchRequest(List<Float> vector, String indexName, String vectorName) {
        return SearchRequest.of(searchRequest ->
                searchRequest.index(indexName)
                        .query(query -> query
                                .knn(knn -> knn
                                        .field(vectorName)
                                        .vector(convertToArray(vector))
                                        .k(KNN_K)
                                )
                        )
        );
    }

    private SearchRequest createKnnSearchRequest(List<Float> vector, String indexName, String vectorName, int size) {
        return SearchRequest.of(searchRequest ->
                searchRequest.index(indexName)
                        .size(size)
                        .query(query -> query
                                .knn(knn -> knn
                                        .field(vectorName)
                                        .vector(convertToArray(vector))
                                        .k(KNN_K)
                                )
                        )
        );
    }

    private SearchRequest createKnnSearchRequestForIdsWithFiltering(List<Float> vector, String indexName, String vectorName, int size, String filterName, Long filterValue) {
        return SearchRequest.of(searchRequest ->
                searchRequest.index(indexName)
                        .size(size)
                        .query(query -> query
                                .knn(knn -> knn
                                        .field(vectorName)
                                        .vector(convertToArray(vector))
                                        .k(KNN_K)
                                        .filter(TermQuery.of(term -> term
                                                        .field(filterName)
                                                        .value(FieldValue.of(filterValue)))
                                                ._toQuery()
                                        )
                                )
                        )
        );
    }

    private SearchResponse<JsonData> executeSearch(SearchRequest searchRequest) {
        try {
            return openSearchClient.search(searchRequest, JsonData.class);
        } catch (IOException e) {
            throw new NetworkApiException(NetworkApiErrorCode.OPEN_SEARCH_API_FAIL);
        }
    }

    private List<Long> extractIds(SearchResponse<JsonData> searchResponse, int size) {
        // 검색 결과에서 ID 추출
        try {
            return searchResponse.hits().hits().stream()
                    .map(Hit::id)
                    .map(Long::parseLong)
                    .limit(size)
                    .toList();
        } catch (Exception e) {
            throw new NetworkApiException(NetworkApiErrorCode.OPEN_SEARCH_API_FAIL);
        }
    }

    private List<OpenSearchResultDto> extractResult(SearchResponse<JsonData> searchResponse) {
        try {
            return searchResponse.hits().hits().stream()
                    .map(hit -> OpenSearchResultDto.of(
                            hit.id(),
                            hit.score(),
                            hit.source().toJson().asJsonObject()
                    ))
                    .toList();
        } catch (Exception e) {
            throw new NetworkApiException(NetworkApiErrorCode.OPEN_SEARCH_API_FAIL);
        }
    }

    public static float[] convertToArray(List<Float> list) {
        float[] array = new float[list.size()];
        IntStream.range(0, list.size()).forEach(i -> array[i] = list.get(i));
        return array;
    }
}
