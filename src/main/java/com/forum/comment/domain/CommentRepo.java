package com.forum.comment.domain;

import com.forum.comment.CommentWithSourceTypeResponse;
import com.forum.config.CacheConfig;
import com.forum.util.Utils;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Join;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface CommentRepo extends CrudRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    @Cacheable(cacheNames = CacheConfig.Cache.commentListByThreadIdCache, key = "#threadId + #page.toString() + #pageSize.toString()")
    default Page<Comment> findThreadsByTheadIdPerPageWithCache(Long threadId, int page, int pageSize) {
        List<Comment> comments = findCommentsByThreadId(threadId);
        List<Comment> commentListInPage = comments.stream()
                .skip(Math.multiplyExact(page, pageSize))
                .limit(pageSize)
                .collect(Collectors.toList());
        Pageable pageRequest = PageRequest.of(page, pageSize);
        return new PageImpl<>(commentListInPage, pageRequest, comments.size());
    }

    List<Comment> findCommentsByThreadId(Long threadId);

    @Cacheable(cacheNames = CacheConfig.Cache.commentListBySourceTypeCache, key = "#sourceType + #page.toString() + #pageSize.toString()")
    default Page<CommentWithSourceTypeResponse> findThreadsBySourceTypePerPageWithCache(String sourceType, int page, int pageSize) {
        List<CommentWithSourceTypeResponse> comments = findCommentWithSourceType(sourceType);
        List<CommentWithSourceTypeResponse> commentListInPage = comments.stream()
                .skip(Math.multiplyExact(page, pageSize))
                .limit(pageSize)
                .collect(Collectors.toList());
        Pageable pageRequest = PageRequest.of(page, pageSize);
        return new PageImpl<>(commentListInPage, pageRequest, comments.size());
    }

    @Query(name = "comment_with_source_type_query", nativeQuery = true)
    List<CommentWithSourceTypeResponse> findCommentWithSourceType(
            @Param("sourceTypeParam") String sourceType
    );

    private Map convertRes(Map<String, Object> queryResult) {
        Map<String, Object> returnedObj = new HashMap<>();
        for (Map.Entry<String, Object> field : queryResult.entrySet()) {
            String fieldKey = Utils.toCamelCase(field.getKey());
            returnedObj.put(fieldKey, field.getValue());
        }

        return returnedObj;
    }

    class Spec {
        public static Specification<Comment> byThreadId(Long threadId) {
            return ((root, query, cb) -> root.get("threadId").in(threadId));
        }

        public static Specification<CommentWithSourceTypeResponse> bySourceType(String sourceType) {
            return (root, query, cb) -> {
                Join<Comment, Thread> commentInSourceType = root.join("forum_thread");
                return cb.equal(commentInSourceType.get("source_type"), sourceType);
            };
        }
    }
}
