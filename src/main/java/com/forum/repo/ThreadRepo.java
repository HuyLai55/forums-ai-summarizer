package com.forum.repo;

import com.forum.domain.Thread;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ThreadRepo extends CrudRepository<Thread, Long>, JpaSpecificationExecutor<Thread> {
    Thread findByThreadUrl(String url);

}