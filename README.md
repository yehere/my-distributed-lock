# my-distributed-lock
分布式锁组件
项目下面那个lock包就是分布式锁的组件源码  
实现方式：这里提供了redis和zookeeper两种实现方式  
使用：支持注解/lock编程模型  
可重入性：支持  

# 1.使用
## 1.1：添加pom依赖：
```java
<dependency>  
    <groupId>com.github.13872095752</groupId>  
    <artifactId>compotent-lock</artifactId>  
    <version>1.0.0-RELEASE</version>  
</dependency>  
```
如果没有redis依赖需要加上下面的依赖：  
```java
<dependency>  
    <groupId>org.springframework.boot</groupId>  
    <artifactId>spring-boot-starter-data-redis</artifactId>  
    <version>2.2.3.RELEASE</version>  
</dependency>
```

使用redis方式，正常redis连接配置  
使用zk方式需要在配置文件加入：  
```
zookeeper:  
  host: ip:port
```


## 1.2 使用方式：
### 1.2.1 注解方式(推荐，默认有key生成规则，也可自己指定）
、、、
@DistributedLock
public xxx xxxmethod(xxx x){
	xxx
	...
}
、、、

### 1.2.2 直接调用api
```java
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
```


# 2.注意点：
**·目前的分布式锁重入性不支持跨服务器使用：例如两个接口a，b 处于不同的进程中，它们共用同一把锁，a接口请求b的时候会导致死锁**  
**·如果不使用注解方式或者使用注解方式自定义key，一定要注意key的命名冲突问题，如果不是共用一把锁不要使用同一个key，避免不必要的锁竞争。**

# 3.相关实现
## 3.1 redis实现：
redis的setnx操作是一个原子操作，成功返回1，失败返回0，根据这个判定锁的获取，加上失效时常及异常处理防止出现死锁  
线程休眠一段时间轮询判定锁的状态（休眠时常可全局修改，也可使用时指定，根据具体业务场景评估，合理的设置可减少redis性能损耗）  
释放锁就是删除key，利用脚本实现防止线程安全问题  
## 3.2 zookeeper实现：
获取锁的时候插入有序节点，当前插入的节点是最小节点作为锁的获取条件  
没有获取到锁的线程监听前面节点的删除动作，也就是锁的释放  
锁的释放通过删除当前节点完成  

# 4.优化
性能优化：在获取锁之前做内存锁的竞争，减少分布式锁的压力  
易用性：支持注解方式  
可扩展性：面向接口编程，实现代码的可插拔，支持其他实现lock接口的插件接入  

