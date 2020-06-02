//package com.example.redissentinel.lock;
//
//import com.fulu.game.component.core.bean.LockKey;
//import com.fulu.game.component.core.manager.config.Configuration;
//import com.fulu.game.component.core.manager.config.RedisConfiguration;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.script.DefaultRedisScript;
//
//import java.util.Arrays;
//import java.util.UUID;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//
///**
// * @Author heye
// * @Date 2020/01/10
// * @Decription 分布式锁redis实现
// */
//public class RedisLock implements Lock {
//
//    private RedisTemplate redisTemplate;
//
//    private ThreadLocal<String> local;
//
//    private RedisConfiguration conf;
//
//    private String key;//锁的key
//
//    public RedisLock(RedisConfiguration configuration, String key) {
//        local = new ThreadLocal<>();
//        this.redisTemplate = configuration.getRedisTemplate();
//        this.conf = configuration;
//        this.key = key;
//    }
//
//    @Override
//    public void lock() {
//        LockKey lockKey = Configuration.keyMap.get(key);
//        //1.尝试加锁
//        if ((lockKey != null && lockKey.acquireIncr())
//                || acquireLock()) {
//            return;
//        }
//
//        //2.加锁失败，当前任务休眠一段时间
//        try {
//            Thread.sleep(conf.getWaitMillis());//性能浪费
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        //3.递归调用，再次去抢锁
//        lock();
//    }
//
//    public boolean acquireLock() {
//        //产生随机值，标识本次锁编号
//        String uuid = UUID.randomUUID().toString();
//        boolean result = redisTemplate.opsForValue().setIfAbsent(key, uuid);
//        if(result) {
//            Configuration.keyMap.put(key, LockKey
//                    .builder()
//                    .key(key).prefix(conf.getPrefix()).suffix(conf.getSuffix())
//                    .build());
//            local.set(uuid);
//            redisTemplate.expire(key, conf.getExpireMillis(), TimeUnit.MILLISECONDS);
//        }
//        return result;
//    }
//
//    @Override
//    public void unlock() {
//        redisTemplate.execute(
//                new DefaultRedisScript(conf.getScript(), Long.class),
//                Arrays.asList(key),
//                Arrays.asList(local.get())
//        );
//        Configuration.keyMap.remove(key);
//    }
//
//    @Override
//    public void lockInterruptibly() throws InterruptedException {
//    }
//
//    @Override
//    public boolean tryLock() {
//        return false;
//    }
//
//    @Override
//    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
//        return false;
//    }
//
//    @Override
//    public Condition newCondition() {
//        return null;
//    }
//}
