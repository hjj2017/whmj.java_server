package org.mj.comm.pubsub;

import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * 自定义发布者
 */
public final class MyPublisher {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MyPublisher.class);

    /**
     * 类默认构造器
     */
    public MyPublisher() {
    }

    /**
     * 发布
     *
     * @param channel 频道
     * @param strMsg  字符串消息
     */
    public void publish(String channel, String strMsg) {
        if (null == channel ||
            null == strMsg) {
            return;
        }

        try (Jedis redisPubsub = RedisXuite.getRedisPubSub()) {
            redisPubsub.publish(
                channel, strMsg
            );
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
