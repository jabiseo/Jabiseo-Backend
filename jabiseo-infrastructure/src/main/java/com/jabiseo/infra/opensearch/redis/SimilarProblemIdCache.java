package com.jabiseo.infra.opensearch.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash(value = "similarProblemId", timeToLive = 2592000)
@Getter
@AllArgsConstructor
public class SimilarProblemIdCache {

    @Id
    private Long problemId;

    private List<Long> similarProblemIds;

}
