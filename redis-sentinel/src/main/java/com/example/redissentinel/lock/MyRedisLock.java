package com.example.redissentinel.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @Author heye
 * @Date 2020/01/04
 * @Decription 分布式锁实现
 */
@Service
public class MyRedisLock {
	
	@Autowired
	private RedisTemplate redisTemplate;

	private ThreadLocal<String> local = new ThreadLocal<>();
	
	
	public void lock(String key,long millis) {
		//1.尝试加锁
		if(tryLock(key,millis)){
			return;
		}
		//2.加锁失败，当前任务休眠一段时间
		try {
			Thread.sleep(10);//性能浪费
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//3.递归调用，再次去抢锁
		lock(key,millis);
	}

	private boolean tryLock(String key,long millis) {
		//产生随机值，标识本次锁编号
		String uuid = UUID.randomUUID().toString();
		return redisTemplate.opsForValue().setIfAbsent(key, uuid, millis, TimeUnit.MILLISECONDS);
	}

	public void unlock(String key) {
		String script = FileUtils.getScript("unlock.lua");
		redisTemplate.execute(
				new DefaultRedisScript(script, Long.class),
				Arrays.asList(key),
				Arrays.asList(local.get())
		);
	}


	public Condition newCondition() {
		return null;
	}
	
	public boolean tryLock(long time, TimeUnit unit)
			throws InterruptedException {
		return false;
	}

	public void lockInterruptibly() throws InterruptedException {
	}

}
