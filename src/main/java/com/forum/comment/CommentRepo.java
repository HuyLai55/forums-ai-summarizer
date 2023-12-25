package com.forum.comment;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepo extends CrudRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    class Spec {
        public static Specification<Comment> byThreadId(Long threadId) {
            return ((root, query, cb) -> root.get("threadId").in(threadId));
        }
    }
}
