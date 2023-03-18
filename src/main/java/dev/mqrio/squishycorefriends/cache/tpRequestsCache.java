package dev.mqrio.squishycorefriends.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class tpRequestsCache {
    Map<String, tpRequestsCacheEntry> tpRequestsCache = new HashMap<String, tpRequestsCacheEntry>();

    public boolean hasKey(Object key) {
        return tpRequestsCache.containsKey(key);
    }

    public tpRequestsCacheEntry put(String key, tpRequestsCacheEntry value) {
        return tpRequestsCache.put(key, value);
    }

    public tpRequestsCacheEntry remove(Object key) {
        return tpRequestsCache.remove(key);
    }

    public tpRequestsCacheEntry get(Object key) {
        return tpRequestsCache.get(key);
    }

    public Set<String> keySet() {
        return tpRequestsCache.keySet();
    }
}