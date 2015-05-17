package edu.sjsu.cmpe.cache.client.CacheService;

/**
 * Cache Service Interface
 * 
 */
public interface ServiceInterfaceCache {
    public String get(long key);

    public void put(long key, String value);
}
