package org.mj.comm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式锁, 借助 Redis 实现
 */
public final class DLock implements AutoCloseable {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(DLock.class);

    /**
     * 分布式锁字典
     */
    static private final Map<String, DLock> _dLockMap = new ConcurrentHashMap<>();

    /**
     * Redis 关键字前缀
     */
    static public String _redisKeyPrefix = "";

    /**
     * 锁名称
     */
    private final String _name;

    /**
     * 创建时间
     */
    private final long _createTime;

    /**
     * 持续时间
     */
    private long _duration;

    /**
     * 私有化类参数构造器
     *
     * @param name 锁名称
     */
    private DLock(String name) {
        _name = name;
        _createTime = System.currentTimeMillis();
    }

    /**
     * 获取分布式锁
     *
     * @param name 锁名称
     * @return 分布式锁
     */
    static public DLock newLock(String name) {
        if (null == name ||
            name.isEmpty()) {
            throw new IllegalArgumentException("name is null or empty");
        }

        // 事先执行自动清理
        autoClean();

        if (!_dLockMap.containsKey(name)) {
            _dLockMap.putIfAbsent(name, new DLock(name));
        }

        return _dLockMap.get(name);
    }

    /**
     * 尝试加锁
     *
     * @param duration 持续时间 ( 毫秒数 )
     * @return true = 加锁成功, false = 加锁失败
     */
    public boolean tryLock(long duration) {
        if (duration < 0) {
            duration = 1;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            if (null == redisCache) {
                LOGGER.error("redisCache is null");
                return false;
            }

            // 通过 Redis 加锁
            Long succezz = redisCache.setnx(getRedisKey(), "yes");

            if (succezz > 0) {
                redisCache.pexpire(getRedisKey(), duration);
                _duration = duration;
                return true;
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return false;
    }

    /**
     * 释放锁
     */
    public void release() {
        // 从字典中移除
        _dLockMap.remove(_name);

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            if (null == redisCache) {
                LOGGER.error("redisCache is null");
                return;
            }

            redisCache.del(getRedisKey());
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void close() {
        release();
    }

    /**
     * 自动清理
     */
    static private void autoClean() {
        // 获取当前时间
        final long nowTime = System.currentTimeMillis();

        // 清理掉已经超时的
        _dLockMap.values().removeIf((dLock) ->
            null == dLock || nowTime - dLock._createTime > dLock._duration
        );
    }

    /**
     * 关键 Redis 关键字
     *
     * @return Redis 关键字
     */
    private String getRedisKey() {
        return _redisKeyPrefix + _name;
    }
}
