package com.forum.repo;

import com.forum.domain.ThreadSummary;
import org.springframework.data.repository.CrudRepository;

public interface ThreadSummaryRepo extends CrudRepository<ThreadSummary, Long> {
    ThreadSummary findByThreadId(Long threadId);
}
