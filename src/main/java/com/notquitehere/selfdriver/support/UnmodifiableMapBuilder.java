package com.notquitehere.selfdriver.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UnmodifiableMapBuilder<K, V> {

    private Map<K,V> map;
    private boolean inProgress = true;

    public UnmodifiableMapBuilder() {
        map = new HashMap<>();
    }

    public UnmodifiableMapBuilder<K, V> add(K key, V value) {
        check();
        map.put(key, value);
        return this;
    }

    public Map<K,V> build() {
        check();
        Map<K, V> payload = Collections.unmodifiableMap(map);
        inProgress = false;
        return payload;
    }

    private void check() {
        if (!inProgress) {
            throw new IllegalStateException("Map already built");
        }
    }
}
