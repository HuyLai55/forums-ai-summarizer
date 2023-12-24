package com.forums.thread;

import org.springframework.data.repository.CrudRepository;

public interface ThreadRepo extends CrudRepository<Thread,Long> {
}
