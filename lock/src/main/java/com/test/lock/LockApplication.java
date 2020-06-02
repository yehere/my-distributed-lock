package com.test.lock;

import com.test.lock.condition.ZkCondition;
import com.test.lock.core.manager.LockManager;
import com.test.lock.core.manager.RedisLockManager;
import com.test.lock.core.manager.ZookeeperLockManager;
import com.test.lock.core.manager.config.RedisConfiguration;
import com.test.lock.core.manager.config.ZookeeperConfiguration;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;

@Configuration
//@EnableCaching
//@ComponentScan("com.test.lock.aspect")
@SpringBootApplication
public class LockApplication {
//
    public static void main(String[] args) {
        SpringApplication.run(LockApplication.class, args);
    }
//
    @Bean
    @Conditional(ZkCondition.class)
    public ZkClient zkClient(ApplicationContext context){
        return new ZkClient(context.getEnvironment().getProperty("zookeeper.host"));
    }
//
    @Bean
    @Conditional(ZkCondition.class)
    @ConditionalOnMissingBean(name = "zookeeperLockManager")
    public LockManager zookeeperLockManager(ZkClient zkClient){
//        System.out.println("\n\n扫描到了\n\n\n");
        return ZookeeperLockManager
                .builder(new ZookeeperConfiguration(zkClient))
                .prefix("/lock")
                .enableStatis(true)
                .expireMillis(1000 * 60 * 10)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name="redisLockManager")
    public LockManager redisLockManager(RedisTemplate redisTemplate){
        return RedisLockManager
                .builder(new RedisConfiguration(redisTemplate))
                .prefix("lock:")
                .enableStatis(true)
                .expireMillis(1000 * 60 * 10).waitMillis(10L)
                .build();
    }
/*
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(valueSerializer());
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    private RedisSerializer<Object> valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }




    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                                .entryTtl(Duration.ofSeconds(200))) //缓存时间绝对过期时间 20s
                .transactionAware().build();
    }

    @Bean
    public CacheManager cacheManager2(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                                .entryTtl(Duration.ofSeconds(2000))) //缓存时间绝对过期时间 20s
                .transactionAware().build();
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getSimpleName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

*/


}
