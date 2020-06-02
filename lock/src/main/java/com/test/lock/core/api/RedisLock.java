package com.test.lock.core.api;

import com.test.lock.core.manager.config.RedisConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @Author heye
 * @Date 2020/01/10
 * @Decription 分布式锁redis实现
 */
public class RedisLock extends AbstractLock<RedisConfiguration> {

    private RedisTemplate redisTemplate;

    private ThreadLocal<String> local;

    private long waitMillis;

    private long expireMillis;

    public RedisLock(RedisConfiguration configuration, String key, long waitMillis, long expireMillis) {
        local = new ThreadLocal<>();
        this.redisTemplate = configuration.getRedisTemplate();
        this.waitMillis = waitMillis;
        this.expireMillis = expireMillis;
        this.conf = configuration;
        this.key = key;
        this.lockType = this.getClass().getSimpleName();
    }

    @Override
    public boolean tryLock() {
        //1.尝试加锁
        return acquireLock();
    }

    public boolean acquireLock() {
        //产生随机值，标识本次锁编号
        String uuid = UUID.randomUUID().toString();
        boolean result = redisTemplate.opsForValue().setIfAbsent(key, uuid);
        if (result) {
            local.set(uuid);
            redisTemplate.expire(key, expireMillis, TimeUnit.MILLISECONDS);
        }
        return result;
    }

    @Override
    public void waitLock() {
        //2.加锁失败，当前任务休眠一段时间
        try {
            Thread.sleep(waitMillis);//性能浪费
        } catch (InterruptedException e) {
            logger.info("线程被中断", e);
        }
    }

    @Override
    public void unlock() {
        logger.info("##释放lock锁的资源####");
        //删除key并删除关联资源
        Long result = (Long)redisTemplate.execute(
                new DefaultRedisScript(conf.getScript(), Long.class),
                Arrays.asList(key),
                local.get()
        );
        local.remove();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
