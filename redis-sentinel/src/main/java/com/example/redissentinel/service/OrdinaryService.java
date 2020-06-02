package com.example.redissentinel.service;

import com.example.redissentinel.dao.SimpleDao;
import com.example.redissentinel.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author heye
 * @Date 2019/12/26
 * @Decription
 */
@Service
public class OrdinaryService {
    @Autowired
    private SimpleDao simpleDao;

    public UserInfo findById(String userId) {
        System.out.println("进行数据库查询。。");
        return simpleDao.findById(userId);
    }

    public UserInfo update(UserInfo userInfo) {
        simpleDao.update(userInfo);
        return userInfo;
    }

    public void delete(String userId) {
        simpleDao.delete(userId);
    }

}
