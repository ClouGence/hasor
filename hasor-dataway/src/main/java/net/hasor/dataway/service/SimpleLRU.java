package net.hasor.dataway.service;
import java.util.LinkedHashMap;
import java.util.Map;

// 继承LinkedHashMap
class SimpleLRU<K, V> extends LinkedHashMap<K, V> {
    private final int MAX_CACHE_SIZE;

    public SimpleLRU(int cacheSize) {
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        MAX_CACHE_SIZE = cacheSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > MAX_CACHE_SIZE;
    }
}