package com.test.lock.core.manager.config;

import com.test.lock.util.FileUtils;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author heye
 * @Date 2020/1/10
 * @Decription 存放redis分布式锁的主要配置
 */
@Data
public class RedisConfiguration extends BaseConfiguration{

    public RedisConfiguration(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
        this.script = FileUtils.getScript("unlock.lua");
    }

    private String script;

    private Long expireMillis = 10 * 60 * 1000L;

    private Long waitMillis = 10L;

    private RedisTemplate redisTemplate;

    private String prefix = "lock:";

    private String suffix;

}
