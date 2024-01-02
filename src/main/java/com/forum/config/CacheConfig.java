package com.forum.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class CacheConfig {
    @Bean
    public CacheManager customize() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<CaffeineCache> caches = new CopyOnWriteArrayList<>();

        caches.add(new CaffeineCache(Cache.threadListCache, Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build()));
        caches.add(new CaffeineCache(Cache.commentListByThreadIdCache, Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build()));
        caches.add(new CaffeineCache(Cache.commentListBySourceTypeCache, Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build()));

        cacheManager.setCaches(caches);
        return cacheManager;
    }

    public static class Cache {
        public static final String threadListCache = "threadListCache";
        public static final String commentListByThreadIdCache = "commentListByThreadIdCache";
        public static final String commentListBySourceTypeCache = "commentListBySourceTypeCache";
    }
}
