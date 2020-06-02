package com.test.lock.core.manager.config;

import lombok.Data;
import org.I0Itec.zkclient.ZkClient;

/**
 * @Author heye
 * @Date 2020/1/13
 * @Decription 存放zookeeper分布式锁的主要配置
 */
@Data
public class ZookeeperConfiguration extends BaseConfiguration {

    public ZookeeperConfiguration(ZkClient zkClient){
        this.zkClient = zkClient;
    }

    /**锁的超时时长*/
    private Long expireMillis = 10 * 60 * 1000L;

    private ZkClient zkClient;

    private String prefix = "/lock";

    private String suffix;

}
