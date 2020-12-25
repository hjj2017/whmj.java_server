package org.mj.comm.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis 工具类
 */
public final class RedisXuite {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RedisXuite.class);

    /**
     * 职位类型
     */
    private enum JobType {
        /**
         * 缓存
         */
        cache,

        /**
         * 消息发布订阅
         */
        pubsub,

        /**
         * 消息队列
         */
        mq,
    }

    /**
     * Redis 连接池字典
     */
    static private final Map<JobType, JedisPool> _jedisPoolMap = new ConcurrentHashMap<>();

    /**
     * 使用配置
     */
    static private Config _usingConf = null;

    /**
     * 私有化类默认构造器
     */
    private RedisXuite() {
    }

    /**
     * 初始化Redis连接池
     *
     * @param usingConf 使用配置
     */
    static public void init(Config usingConf) {
        if (null == usingConf ||
            null == usingConf._itemMap) {
            throw new IllegalArgumentException("usingConf or usingConf._itemMap is null");
        }

        _usingConf = usingConf;
        _usingConf._itemMap.forEach((strKey, item) -> {
            if (null == strKey ||
                null == item) {
                return;
            }

            // 获取职位类型
            JobType jt = JobType.valueOf(strKey);

            try {
                JedisPool newPool = new JedisPool(
                    item._serverHost,
                    item._serverPort
                );

                if (item._dbIndex > 0) {
                    LOGGER.info(
                        "Redis 连接成功! jobType = {}, addr = {}:{}, dbIndex = {}",
                        jt.name(),
                        item._serverHost,
                        item._serverPort,
                        item._dbIndex
                    );
                } else {
                    LOGGER.info(
                        "Redis 连接成功! jobType = {}, addr = {}:{}",
                        jt.name(),
                        item._serverHost,
                        item._serverPort
                    );
                }

                _jedisPoolMap.put(jt, newPool);
            } catch (Exception ex) {
                // 记录错误日志
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * 获取 Redis 实例
     *
     * @param jt 职位类型
     * @return Redis 实例
     */
    static private Jedis getRedisX(JobType jt) {
        if (null == jt) {
            throw new IllegalArgumentException("jt is null");
        }

        JedisPool pool = _jedisPoolMap.get(jt);

        if (null == pool) {
            throw new RuntimeException(
                "jedisPool 尚未初始化, jobType = " + jt.name()
            );
        }

        Jedis newRedis = pool.getResource();

        // 获取配置项
        Config.Item confItem = _usingConf._itemMap.get(jt.name());

        if (null != confItem) {
            // 获取 Redis 密码
            final String password = confItem._password;

            if (null != password &&
                !password.isEmpty()) {
                // 授权 Redis
                newRedis.auth(confItem._password);
            }

            // 获取数据库索引
            final int dbIndex = confItem._dbIndex;

            if (dbIndex > 0) {
                // 使用指定数据库
                newRedis.select(dbIndex);
            }
        }

        return newRedis;
    }

    /**
     * 获取 Redis 缓存
     *
     * @return Redis 对象
     */
    static public Jedis getRedisCache() {
        return getRedisX(JobType.cache);
    }

    /**
     * 获取 Redis 发布订阅
     *
     * @return Redis 对象
     */
    static public Jedis getRedisPubSub() {
        return getRedisX(JobType.pubsub);
    }

    /**
     * 获取 Redis 消息队列
     *
     * @return Redis 对象
     */
    static public Jedis getRedisMq() {
        return getRedisX(JobType.mq);
    }

    /**
     * Redis 套件配置
     */
    static public class Config {
        /**
         * 配置项字典
         */
        public Map<String, Item> _itemMap = null;

        /**
         * // 从 JSON 对象中创建 Redis 套件配置
         *
         * @param jsonObj JSON 对象
         * @return Redis 套件配置
         */
        static public Config fromJSONObj(JSONObject jsonObj) {
            if (null == jsonObj) {
                return null;
            }

            // Redis 套件配置
            JSONObject joRedisXuite = jsonObj.getJSONObject("redisXuite");

            if (null == joRedisXuite) {
                return null;
            }

            Config newConf = new Config();
            newConf._itemMap = new ConcurrentHashMap<>();

            for (JobType jt : JobType.values()) {
                // 获取配置项
                JSONObject joItem = joRedisXuite.getJSONObject(jt.name());
                // 从 JSON 对象中创建配置项
                Item newItem = Item.fromJSONObj(joItem);

                if (null != newItem) {
                    newConf._itemMap.put(jt.name(), newItem);
                }
            }

            return newConf;
        }

        /**
         * 配置项
         */
        static public class Item {
            /**
             * Redis 服务器主机地址
             */
            @JSONField(name = "serverHost")
            public String _serverHost = "";

            /**
             * Redis 服务器端口号
             */
            @JSONField(name = "serverPort")
            public int _serverPort = 6379;

            /**
             * 数据库索引
             */
            @JSONField(name = "dbIndex")
            public int _dbIndex = 0;

            /**
             * 密码
             */
            @JSONField(name = "password")
            public String _password = null;

            /**
             * 从 JSON 对象中创建配置项
             *
             * @param jsonObj JSON 对象
             * @return 配置项
             */
            static public Item fromJSONObj(JSONObject jsonObj) {
                if (null == jsonObj) {
                    return null;
                }

                return jsonObj.toJavaObject(Item.class);
            }
        }
    }
}
