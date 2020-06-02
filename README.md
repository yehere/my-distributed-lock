# my-distributed-lock
分布式锁组件
下面那个lock就是分布式锁的组件，可以打成jar给项目引用，后续我会上传到仓库给出maven依赖，这里提供了redis和zookeeper两种实现方式，支持注解

1.功能
相关实现
redis实现：
redis的setnx操作是一个原子操作，成功返回1，失败返回0，根据这个判定锁的获取，加上失效时常及异常处理防止出现死锁
线程休眠一段时间轮询判定锁的状态（休眠时常可全局修改，也可使用时指定，根据具体业务场景评估，合理的设置可减少redis性能损耗）
释放锁就是删除key，利用脚本实现防止线程安全问题
zookeeper实现：
获取锁的时候插入有序节点，当前插入的节点是最小节点作为锁的获取条件
没有获取到锁的线程监听前面节点的删除动作，也就是锁的释放
锁的释放通过删除当前节点完成
对比

实现方式	使用复杂度	资源利用率	时效性	tps
redis实现	低	较低（合理的休眠时间可提高利用率）	较及时	10w+/s
zookeeper实现	低	高	及时	1w/s


2.相关优化

性能优化：在获取锁之前做内存锁的竞争，减少分布式锁的压力
易用性：支持注解方式
可扩展性：面向接口编程，实现代码的可插拔

3.使用

打入maven仓库，工程引入即可

添加pom依赖：
待补充


下面两个按需引入：（使用redis方式需要引入redis依赖，zk方式需要引入zk的依赖）

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.10</version>
</dependency>

zk方式需要在配置文件加入：
zookeeper:
  host: ip:port


使用方式：
注解方式(推荐)

@DistributedLock
public xxx xxxmethod(xxx x){
	xxx
	...
}



直接调用api

@Autiwried
private LockManager lockManager;

public xxx xxmethod(xxx x 。。。){
	Lock lock = lockManager.getLock(key);
	try {
    	lock.lock();//阻塞获取分布式锁
    	result = xxx;//执行业务逻辑
	} catch (Exception e) {
	} finally {
    	lock.unlock();
	}
	return result;
}




4.注意点：

目前的分布式锁重入性在跨服务调用的时候不可用：例如两个接口a，b 处于不同的进程中，它们共用同一把锁，a接口请求b的时候会导致死锁
如果不使用注解方式或者使用注解方式自定义key，一定要注意key的命名冲突问题，如果不是共用一把锁不要使用同一个key，会造成性能问题。

