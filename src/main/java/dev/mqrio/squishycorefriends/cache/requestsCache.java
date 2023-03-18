package dev.mqrio.squishycorefriends.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class requestsCache {
    Map<String, requestsCacheEntry> requestsCache = new HashMap<String, requestsCacheEntry>();

    public boolean hasKey(Object key) {
        return requestsCache.containsKey(key);
    }

    public requestsCacheEntry put(String key, requestsCacheEntry value) {
        return requestsCache.put(key, value);
    }

    public requestsCacheEntry remove(Object key) {
        return requestsCache.remove(key);
    }

    public requestsCacheEntry get(Object key) {
        return requestsCache.get(key);
    }

    public Set<String> keySet() {
        return requestsCache.keySet();
    }
}