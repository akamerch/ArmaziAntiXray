package dev.armazi.antixray.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * High-performance cache management using Caffeine
 */
public class CacheManager {
    
    private final Cache<String, Object> generalCache;
    private final Cache<String, byte[]> chunkCache;
    private final Cache<String, Long> playerCache;

    public CacheManager() {
        this.generalCache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();
            
        this.chunkCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(50000)
            .build();
            
        this.playerCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(5000)
            .build();
    }

    public void put(String key, Object value) {
        generalCache.put(key, value);
    }

    public Object get(String key) {
        return generalCache.getIfPresent(key);
    }

    public void putChunk(String key, byte[] data) {
        chunkCache.put(key, data);
    }

    public byte[] getChunk(String key) {
        return chunkCache.getIfPresent(key);
    }

    public void putPlayer(String key, long value) {
        playerCache.put(key, value);
    }

    public Long getPlayer(String key) {
        return playerCache.getIfPresent(key);
    }

    public void clearAll() {
        generalCache.invalidateAll();
        chunkCache.invalidateAll();
        playerCache.invalidateAll();
    }

    public long size() {
        return generalCache.estimatedSize() + chunkCache.estimatedSize() + playerCache.estimatedSize();
    }
}