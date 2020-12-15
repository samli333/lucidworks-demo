package com.ferguson.feedengine.batch.utils;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    private Map<Object, Object> holder = new HashMap<>();


    public void put(Object key, Object value) {
        holder.put(key, value);
    }

    public Object get(Object key) {
        return holder.get(key);
    }

}
