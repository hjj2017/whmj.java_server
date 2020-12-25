package org.mj.comm.pubsub;

import org.mj.comm.util.RedisXuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 自定义订阅者
 */
public final class MySubscriber {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MySubscriber.class);

    /**
     * 订阅指定频道
     *
     * @param chArray 频道数组
     * @param h       消息处理器
     * @throws IllegalArgumentException if null == chArray or chArray.length <= 0
     * @throws IllegalArgumentException if null == h
     */
    public void subscribe(String[] chArray, IMsgHandler h) {
        if (null == chArray ||
            chArray.length <= 0) {
            throw new IllegalArgumentException("chArray is null or empty");
        }

        if (null == h) {
            throw new IllegalArgumentException("h is null");
        }

        // 记录日志信息
        LOGGER.info(
            "开启订阅, channelArray = [ {} ]",
            String.join(", ", chArray)
        );

        // 创建线程池
        final ExecutorService es = Executors.newSingleThreadExecutor((r) -> {
            Thread t = new Thread(r);
            t.setName("mySubscriber");
            return t;
        });
        es.submit(() -> {
            try (Jedis redis = RedisXuite.getRedisPubSub()) {
                redis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String strMsg) {
                        onMsg(channel, strMsg, h);
                    }
                }, chArray);
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        });
    }

    /**
     * 当接到消息时
     *
     * @param ch     频道
     * @param strMsg 字符串消息
     * @param h      消息处理器
     */
    static private void onMsg(String ch, String strMsg, IMsgHandler h) {
        if (null == ch ||
            null == strMsg ||
            null == h) {
            return;
        }

        try {
            // 处理消息
            h.handle(ch, strMsg);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 消息处理器接口
     */
    public interface IMsgHandler {
        /**
         * 处理消息
         *
         * @param channel 频道
         * @param strMsg  字符串消息
         */
        void handle(String channel, String strMsg);
    }

    /**
     * 消息处理器组
     */
    static public class MsgHandlerGroup implements IMsgHandler {
        /**
         * 消息处理器列表
         */
        private final List<IMsgHandler> _hList = new ArrayList<>();

        /**
         * 添加消息处理器
         *
         * @param h 消息处理器
         * @return this 指针
         */
        public MsgHandlerGroup add(IMsgHandler h) {
            if (null != h) {
                this._hList.add(h);
            }

            return this;
        }

        @Override
        public void handle(String ch, String strMsg) {
            if (null == ch ||
                null == strMsg) {
                return;
            }

            for (IMsgHandler h : _hList) {
                if (null != h) {
                    h.handle(ch, strMsg);
                }
            }
        }
    }
}
