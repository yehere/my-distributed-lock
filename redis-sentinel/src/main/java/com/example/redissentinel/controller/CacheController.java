package com.example.redissentinel.controller;

import com.example.redissentinel.model.UserInfo;
import com.example.redissentinel.service.OrdinaryService;
import com.test.lock.core.annotation.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author heye
 * @Date 2019/12/26
 * @Decription
 */
@Controller
public class CacheController {
    @Autowired
    public OrdinaryService ordinaryService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/userInfo/find")
    @ResponseBody
    @DistributedLock
    public UserInfo findUserInfo(String userId) {
        System.out.printf("request userId:%s \n", userId);
        return ordinaryService.findById("111");
    }

    @GetMapping("/userInfo/update")
    @ResponseBody
    public void update() {
        ordinaryService.update(new UserInfo().toBuilder().userId("111").name("dfdfdf").build());
    }

    @GetMapping("/userInfo/delete")
    @ResponseBody
    public void delete() {
        ordinaryService.delete("111");
    }

}
