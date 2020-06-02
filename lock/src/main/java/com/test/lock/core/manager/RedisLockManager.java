package com.test.lock.core.manager;

import com.test.lock.core.api.RedisLock;
import com.test.lock.core.manager.config.RedisConfiguration;
import com.test.lock.core.manager.config.TempConfig;
import com.test.lock.util.TransferUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.locks.Lock;

/**
 * @Author heye
 * @Date 2020/1/10
 * @Decription
 */
public class RedisLockManager extends LockManager<RedisConfiguration> {
    private RedisTemplate redisTemplate;

    public static RedisLockManagerBuilder builder(RedisConfiguration configuration) {
        Assert.notNull(configuration, "configuration must not be null!");
        return RedisLockManagerBuilder.fromConfiguration(configuration);
    }

    private RedisLockManager(RedisConfiguration configuration) {
        this.conf = configuration;
    }

    @Override
    public Lock getLock() {
        return null;
    }

    /**
     * the full-args-constructor of Lock
     *
     * @param key
     * @param waitMillis
     * @param expireMillis
     * @return
     */
    public Lock getLock(String key, Long waitMillis, Long expireMillis) {
        Assert.notNull(key, "key must not be null!");
        waitMillis = waitMillis == null ? conf.getWaitMillis() : waitMillis;
        expireMillis = expireMillis == null ? conf.getExpireMillis() : expireMillis;
        key = (conf.getPrefix() == null ? "" : conf.getPrefix()) + key + (conf.getSuffix() == null ? "" : conf.getSuffix());

        return wapperReentrant(new RedisLock(conf, key, waitMillis, expireMillis));
    }

    @Override
    public Lock getLock(String key) {
        Assert.notNull(key, "key must not be null!");
        return getLock(key, conf.getWaitMillis(), conf.getExpireMillis());
    }

    @Override
    public Lock getLock(String key, TempConfig tc) {
        Assert.notNull(key, "key must not be null!");
        Assert.notNull(tc, "TempConfig must not be null!");
        TransferUtils.fillFromTO(conf, tc);
        key = (tc.getPrefix() == null ? "" : tc.getPrefix()) + key + (tc.getSuffix() == null ? "" : tc.getSuffix());

        return wapperReentrant(new RedisLock(conf, key, tc.getWaitMillis(), tc.getExpireMillis()), tc);
    }

    /**
     * Configurator for creating {@link RedisLockManager}.
     */
    public static class RedisLockManagerBuilder {
        private RedisConfiguration configuration;

        private static RedisLockManagerBuilder fromConfiguration(RedisConfiguration configuration) {
            return new RedisLockManagerBuilder(configuration);
        }

        public RedisLockManagerBuilder expireMillis(long expireMillis) {
            configuration.setExpireMillis(expireMillis);
            return this;
        }

        public RedisLockManagerBuilder prefix(String prefix) {
            configuration.setPrefix(prefix);
            return this;
        }

        public RedisLockManagerBuilder suffix(String suffix) {
            configuration.setSuffix(suffix);
            return this;
        }

        public RedisLockManagerBuilder enableFilter(boolean enableFilter) {
            configuration.setEnableFilter(enableFilter);
            return this;
        }

        public RedisLockManagerBuilder waitMillis(long waitMillis) {
            configuration.setWaitMillis(waitMillis);
            return this;
        }

        public RedisLockManagerBuilder enableStatis(boolean enableStatis) {
            configuration.setEnableStatis(enableStatis);
            return this;
        }

        public RedisLockManager build() {
            return new RedisLockManager(configuration);
        }

        private RedisLockManagerBuilder(RedisConfiguration configuration) {
            this.configuration = configuration;
        }
    }
}
