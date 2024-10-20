package com.jabiseo.infra.opensearch.redis;

import org.springframework.data.repository.CrudRepository;


public interface SimilarProblemIdCacheRepository extends CrudRepository<SimilarProblemIdCache, Long> {
}
