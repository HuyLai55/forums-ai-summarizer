package com.forum.thread.domain;

import com.forum.config.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.stream.Collectors;

public interface ThreadRepo extends CrudRepository<Thread, Long>, JpaSpecificationExecutor<Thread> {
    @Cacheable(cacheNames = CacheConfig.Cache.threadListCache, key = "#sourceType + #page.toString() + #pageSize.toString()")
    default Page<Thread> findThreadsPerPageWithCache(String sourceType, int page, int pageSize) {
        List<Thread> threadList = this.findAll(Spec.bySourceType(sourceType));
        List<Thread> threadListInPage = threadList.stream()
                .skip(Math.multiplyExact(page, pageSize))
                .limit(pageSize)
                .collect(Collectors.toList());
        Pageable pageRequest = PageRequest.of(page, pageSize);
        return new PageImpl<>(threadListInPage, pageRequest, threadList.size());
    }
    class Spec {
        public static Specification<Thread> bySourceTypeAndTitle(String sourceType, String title) {
            return ((root, query, cb) -> cb.and(
                    cb.equal(root.get("sourceType"), sourceType),
                    cb.equal(root.get("title"), title)
            ));
        }

        public static Specification<Thread> bySourceType(String sourceType) {
            return (root, query, cb) -> cb.equal(root.get("sourceType"), sourceType);
        }
    }
}