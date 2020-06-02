package com.test.lock.test.dao;

import com.test.lock.test.model.UserInfo;
import org.springframework.stereotype.Repository;

/**
 * @Author heye
 * @Date 2019/12/26
 * @Decription
 */
@Repository
public class SimpleDao {
    public UserInfo findById(String userId){
        return new UserInfo().toBuilder().name("heye").userId(userId).build();
    }

    public void update(UserInfo userinfo){

    }

    public void delete(String userId){
    }
}
