package org.mj.bizserver.cluster;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.comm.pubsub.MyPublisher;
import org.mj.comm.util.MyTimer;
import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 当前服务器汇报者
 */
public final class CurrServerReporter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(CurrServerReporter.class);

    /**
     * 使用配置
     */
    private final Config _usingConf;

    /**
     * 发布者
     */
    private final MyPublisher _publisher;

    /**
     * 类参数构造器
     *
     * @param usingConf 使用配置
     * @throws IllegalArgumentException if null == usingConf
     */
    public CurrServerReporter(Config usingConf) {
        if (null == usingConf) {
            throw new IllegalArgumentException("usingConf is null");
        }

        _usingConf = usingConf;
        _publisher = new MyPublisher();
    }

    /**
     * 开始上报
     */
    public void startReport() {
        // 延迟时间
        final int delay = _usingConf._timeInterval;
        // 开始定时任务
        MyTimer.scheduleWithFixedDelay(
            this::startUpReport_async, 200, delay, TimeUnit.MILLISECONDS
        );
    }

    /**
     * 开始上报 ( 异步方式 )
     */
    private void startUpReport_async() {
        // 获取回调实现
        final IServerInfoGetter callbackImpl = _usingConf._serverInfoGetter;

        if (null == callbackImpl) {
            // 如果没有设置回调函数,
            LOGGER.error("未设置 '获取服务器信息' 的回调函数");
            return;
        }

        // 获取服务器信息
        ServerInfo newInfo = callbackImpl.get();

        if (null == newInfo) {
            LOGGER.error("服务器信息为空");
            return;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 缓存关键字
            final String redisKey = RedisKeyDef.SERVER_X_PREFIX + newInfo._serverId;
            // 缓存过期时间
            final int expireTime = 10;

            // 设置缓存数据并在合适的时候过期
            redisCache.set(redisKey, JSONObject.toJSONString(newInfo));
            redisCache.expire(redisKey, expireTime);

            // 发布新服务器 Id
            _publisher.publish(
                PubSubChannelDef.NEW_SERVER_COME_IN,
                String.valueOf(newInfo._serverId)
            );
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 配置
     */
    static public final class Config {
        /**
         * 汇报时间间隔
         */
        private int _timeInterval = 2000;

        /**
         * 获取服务器信息接口
         */
        private IServerInfoGetter _serverInfoGetter = null;

        /**
         * 获取汇报时间间隔
         *
         * @return 汇报时间间隔
         */
        public int getTimeInterval() {
            return _timeInterval;
        }

        /**
         * 设置汇报时间间隔
         *
         * @param val 整数值
         */
        public void setTimeInterval(int val) {
            _timeInterval = val;
        }

        /**
         * 获取服务器信息获得者
         *
         * @return 服务器信息获得者
         */
        public IServerInfoGetter getServerInfoGetter() {
            return _serverInfoGetter;
        }

        /**
         * 设置服务器信息获得者
         *
         * @param val 服务器信息获得者
         */
        public void setServerInfoGetter(IServerInfoGetter val) {
            _serverInfoGetter = val;
        }
    }

    /**
     * 服务器信息
     */
    static public final class ServerInfo {
        /**
         * 服务器 Id
         */
        private int _serverId;

        /**
         * 服务器名称
         */
        private String _serverName;

        /**
         * 服务器工作类型
         */
        private Set<String> _serverJobTypeSet;

        /**
         * 服务器主机地址
         */
        private String _serverHost;

        /**
         * 服务器端口号
         */
        private int _serverPort;

        /**
         * 负载数量
         */
        private int _loadCount = -1;

        /**
         * 获取服务器 Id
         *
         * @return 服务器 Id
         */
        @JSONField(name = "serverId")
        public int getServerId() {
            return _serverId;
        }

        /**
         * 设置服务器 Id
         *
         * @param val 整数值
         */
        public void setServerId(int val) {
            _serverId = val;
        }

        /**
         * 获取服务器名称
         *
         * @return 服务器名称
         */
        @JSONField(name = "serverName")
        public String getServerName() {
            return _serverName;
        }

        /**
         * 获取服务器名称
         *
         * @param val 字符串值
         */
        public void setServerName(String val) {
            _serverName = val;
        }

        /**
         * 获取服务器工作类型集合
         *
         * @return 服务器工作类型集合
         */
        @JSONField(name = "serverJobTypeSet")
        public Set<String> getServerJobTypeSet() {
            return Objects.requireNonNullElse(_serverJobTypeSet, Collections.emptySet());
        }

        /**
         * 设置服务器工作类型集合
         *
         * @param val 集合对象
         */
        public void setServerJobTypeSet(Set<String> val) {
            _serverJobTypeSet = val;
        }

        /**
         * 设置服务器工作类型字符串
         *
         * @param val 字符串值
         */
        @JSONField(serialize = false, deserialize = false)
        public void setServerJobTypeStr(String val) {
            if (null == val) {
                return;
            }

            _serverJobTypeSet = Set.of(val.split(","));
        }

        /**
         * 获取服务器主机地址
         *
         * @return 服务器主机地址
         */
        @JSONField(name = "serverHost")
        public String getServerHost() {
            return _serverHost;
        }

        /**
         * 设置服务器主机地址
         *
         * @param val 字符串值
         */
        public void setServerHost(String val) {
            _serverHost = val;
        }

        /**
         * 获取服务器端口号
         *
         * @return 服务器端口号
         */
        @JSONField(name = "serverPort")
        public int getServerPort() {
            return _serverPort;
        }

        /**
         * 设置服务器端口号
         *
         * @param val 整数值
         */
        public void setServerPort(int val) {
            _serverPort = val;
        }

        /**
         * 获取负载数量
         *
         * @return 负载数量
         */
        @JSONField(name = "loadCount")
        public int getLoadCount() {
            return _loadCount;
        }

        /**
         * 设置负载数量
         *
         * @param val 整数值
         */
        public void setLoadCount(int val) {
            _loadCount = val;
        }
    }

    /**
     * 获取服务器信息接口
     */
    public interface IServerInfoGetter {
        /**
         * 获取服务器信息
         *
         * @return 服务器信息
         */
        ServerInfo get();
    }
}
