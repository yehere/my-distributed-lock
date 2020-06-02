package com.test.lock.core.api;


import com.test.lock.core.manager.config.ZookeeperConfiguration;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @Author heye
 * @Date 2020/1/13
 * @Decription
 */
public class ZookeeperLock extends AbstractLock<ZookeeperConfiguration> {
    private ZkClient zkClient;

    private CountDownLatch countDownLatch = null;

    private String beforePath;//当前请求的节点前一个节点

    private String currentPath;//当前请求的节点

    public ZookeeperLock(String key, ZookeeperConfiguration conf) {
        this.zkClient = conf.getZkClient();
        this.key = key;
        this.conf = conf;
        this.lockType = this.getClass().getSimpleName();
    }

    @Override
    public boolean tryLock() {
        return acquireLock();
    }

    public boolean acquireLock() {
        //如果currentPath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        if (currentPath == null || currentPath.length() <= 0) {
            if(!zkClient.exists(key)) {
                if (!zkClient.exists(conf.getPrefix())) {
                    zkClient.createPersistent(conf.getPrefix());
                }
                zkClient.createPersistent(key);
            }
            //创建一个临时顺序节点
            currentPath = this.zkClient.createEphemeralSequential(key+"/", "lock");
        }
        //获取所有临时节点并排序，临时节点名称为自增长的字符串如：0000000400
        List<String> childrens = this.zkClient.getChildren(key);
        Collections.sort(childrens);

        if (currentPath.equals(key+"/" + childrens.get(0))) {//如果当前节点在所有节点中排名第一则获取锁成功
            return true;
        } else {//如果当前节点在所有节点中排名中不是排名第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens,
                    currentPath.substring(7));
            beforePath = key+"/" + childrens.get(wz - 1);
        }
        return false;
    }

    @Override
    public void unlock() {
        //删除当前临时节点
        zkClient.delete(currentPath);
    }

    @Override
    public void waitLock() {
        IZkDataListener listener = new IZkDataListener() {
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }

            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
            }
        };
        //给排在前面的的节点增加数据删除的watcher,本质是启动另外一个线程去监听前置节点
        this.zkClient.subscribeDataChanges(beforePath, listener);

        if (this.zkClient.exists(beforePath)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                logger.info("线程被中断", e);
            }
        }
        this.zkClient.unsubscribeDataChanges(beforePath, listener);
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
