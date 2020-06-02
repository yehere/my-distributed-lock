package com.test.lock.core.manager;

import com.test.lock.core.api.ZookeeperLock;
import com.test.lock.core.manager.config.TempConfig;
import com.test.lock.core.manager.config.ZookeeperConfiguration;
import com.test.lock.util.TransferUtils;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.concurrent.locks.Lock;

/**
 * @Author heye
 * @Date 2020/1/13
 * @Decription
 */
public class ZookeeperLockManager extends LockManager<ZookeeperConfiguration> {
    private ZkClient zkClient;

    private ZookeeperLockManager(ZookeeperConfiguration configuration) {
        this.conf = configuration;
    }

    public static ZookeeperLockManagerBuilder builder(ZookeeperConfiguration configuration) {
        Assert.notNull(configuration, "configuration must not be null!");
        return ZookeeperLockManagerBuilder.fromConfiguration(configuration);
    }

    @Override
    public Lock getLock(String key) {
        Assert.notNull(key, "key must not be null!");
//        key = (conf.getPrefix() == null ? "" : conf.getPrefix()) + '/' + key + (conf.getSuffix() == null ? "" : ('/' + conf.getSuffix()));
        key = (StringUtils.isEmpty(conf.getPrefix()) ? "/" : conf.getPrefix()) + "/" + key + (StringUtils.isEmpty(conf.getSuffix()) ? "" : conf.getSuffix());
//        if (verifyKey(key)) {
////            throw new Error("zk锁的唯一标识设置有误:" + key);
////        }

        return wapperReentrant(new ZookeeperLock(key, conf));
    }

    @Override
    public Lock getLock(String key, TempConfig tc) {
        Assert.notNull(key, "key must not be null!");
        Assert.notNull(tc, "TempConfig must not be null!");
        TransferUtils.fillFromTO(conf, tc);
//        key = (tc.getPrefix() == null ? "" : tc.getPrefix()) + '/' + key + (tc.getSuffix() == null ? "" : ('/' + tc.getSuffix()));
        key = (StringUtils.isEmpty(tc.getPrefix()) ? "/" : tc.getPrefix()) + "/" + key + (StringUtils.isEmpty(tc.getSuffix()) ? "" : tc.getSuffix());
//        if (verifyKey(key)) {
//            throw new Error("zk锁的唯一标识设置有误:" + key);
//        }

        return wapperReentrant(new ZookeeperLock(key, conf), tc);
    }

    private boolean verifyKey(String key) {
        return StringUtils.isEmpty(key) || !key.startsWith("/") || key.endsWith("/") || key.split("/").length > 2;
    }

    @Override
    public Lock getLock() {
        return null;
    }


    /**
     * Configurator for creating {@link ZookeeperLockManager}.
     */
    public static class ZookeeperLockManagerBuilder {
        private ZookeeperConfiguration configuration;

        private ZookeeperLockManagerBuilder(ZookeeperConfiguration configuration) {
            this.configuration = configuration;
        }

        public static ZookeeperLockManagerBuilder fromConfiguration(ZookeeperConfiguration configuration) {
            return new ZookeeperLockManagerBuilder(configuration);
        }

        public ZookeeperLockManagerBuilder prefix(String prefix) {
            this.configuration.setPrefix(prefix);
            return this;
        }

        public ZookeeperLockManagerBuilder suffix(String suffix) {
            this.configuration.setSuffix(suffix);
            return this;
        }

        public ZookeeperLockManagerBuilder expireMillis(long expireMillis) {
            this.configuration.setExpireMillis(expireMillis);
            return this;
        }

        public ZookeeperLockManagerBuilder enableFilter(boolean enableFilter) {
            this.configuration.setEnableFilter(enableFilter);
            return this;
        }

        public ZookeeperLockManagerBuilder enableStatis(boolean enableStatis) {
            this.configuration.setEnableStatis(enableStatis);
            return this;
        }

        public ZookeeperLockManager build() {
            return new ZookeeperLockManager(configuration);
        }
    }

}
