package com.ldjt.emp.framework.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 * 
 * @author emp
 */
@Component
@Slf4j
public class DistributedLock {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取锁对象
     * 
     * @param lockKey 锁的key
     * @return 锁对象
     */
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 加锁
     * 
     * @param lockKey 锁的key
     * @return 锁对象
     */
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 加锁，指定超时时间
     * 
     * @param lockKey 锁的key
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 锁对象
     */
    public RLock lock(String lockKey, long timeout, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    /**
     * 尝试加锁
     * 
     * @param lockKey 锁的key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @return true-加锁成功，false-加锁失败
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            log.error("尝试获取锁失败", e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     * 
     * @param lockKey 锁的key
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 释放锁
     * 
     * @param lock 锁对象
     */
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * 判断锁是否被持有
     * 
     * @param lockKey 锁的key
     * @return true-被持有，false-未被持有
     */
    public boolean isLocked(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        return lock.isLocked();
    }

    /**
     * 执行带锁的操作
     *
     * @param lockKey 锁key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @param supplier 执行的操作
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, 
                                TimeUnit unit, Supplier<T> supplier) {
        RLock lock = getLock(lockKey);
        try {
            if (lock.tryLock(waitTime, leaseTime, unit)) {
                return supplier.get();
            } else {
                throw new RuntimeException("获取锁失败: " + lockKey);
            }
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("获取锁失败: " + lockKey, e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 执行带锁的操作（无返回值）
     *
     * @param lockKey 锁key
     * @param waitTime 等待时间
     * @param leaseTime 锁持有时间
     * @param unit 时间单位
     * @param runnable 执行的操作
     */
    public void executeWithLock(String lockKey, long waitTime, long leaseTime, 
                               TimeUnit unit, Runnable runnable) {
        executeWithLock(lockKey, waitTime, leaseTime, unit, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * 执行带锁的操作（默认参数：等待10秒，持有30秒）
     *
     * @param lockKey 锁key
     * @param supplier 执行的操作
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, 10, 30, TimeUnit.SECONDS, supplier);
    }

    /**
     * 执行带锁的操作（默认参数，无返回值）
     *
     * @param lockKey 锁key
     * @param runnable 执行的操作
     */
    public void executeWithLock(String lockKey, Runnable runnable) {
        executeWithLock(lockKey, 10, 30, TimeUnit.SECONDS, runnable);
    }
}
