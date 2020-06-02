package com.test.lock.core;

import java.util.concurrent.locks.Lock;

/**
 * @Author heye
 * @Date 2020/1/10
 * @Decription
 */
public interface ILockManager {
    Lock getLock();
}
