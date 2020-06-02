package com.example.redissentinel.service;

import com.example.redissentinel.model.UserInfo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @Author heye
 * @Date 2019/12/27
 * @Decription
 */
//@Service
@CacheConfig(cacheNames = "userInfo")
public class CacheService extends OrdinaryService {
    /**
     * 查询的
     * @param userId
     * @return
     */
    @Override
    @Cacheable(cacheManager = "cacheManager2")
    public UserInfo findById(String userId) {
        return super.findById(userId);
    }

    /**
     * 修改的用法
     * @param userInfo
     * @return
     */
    @Override
    @CachePut(key = "#userInfo.userId")
    public UserInfo update(UserInfo userInfo) {
        return super.update(userInfo);
    }

    /**
     * 删除的用法
     * @param userId
     */
    @Override
    @CacheEvict
    public void delete(String userId) {
        super.delete(userId);
    }
}
