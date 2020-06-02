package com.test.lock.core.manager.config;

import com.test.lock.core.bean.LockKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author heye
 * @Date 2020/1/10
 * @Decription
 */

public interface LockContext {
    ConcurrentHashMap<String, LockKey> keyMap = new ConcurrentHashMap<>();

    Map<String, Object> beanMap = new HashMap<>();

}
