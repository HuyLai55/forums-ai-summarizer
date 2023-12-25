package com.forum.thread;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ThreadRepo extends CrudRepository<Thread, Long>, JpaSpecificationExecutor<Thread> {
    class Spec {
        public static Specification<Thread> byUserAndTitle(String userName, String title) {
            return ((root, query, cb) -> cb.and(
                    cb.equal(root.get("userName"), userName),
                    cb.equal(root.get("title"), title)
            ));
        }
    }
}