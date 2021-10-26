package org.mj.proxyserver.base;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Id 设置器和获取器
 */
public class IdSetterGetter {
    /**
     * 会话 Id
     */
    static private final String KEY_SESSION_ID = "mj_session_id";

    /**
     * 用户 Id
     */
    static private final String KEY_USER_ID = "mj_user_id";

    /**
     * Id 生成器
     */
    static private final AtomicInteger _idGen = new AtomicInteger(0);

    /**
     * 私有化类默认构造器
     */
    private IdSetterGetter() {
    }

    /**
     * 设置 X Id
     *
     * @param ch  客户端信道
     * @param key 关键字
     * @param xId Id
     */
    static private void putXId(
        Channel ch, String key, int xId) {
        if (null != ch) {
            ch.attr(AttributeKey.valueOf(key)).setIfAbsent(xId);
        }
    }

    /**
     * 获取 X Id
     *
     * @param ch  客户端信道
     * @param key 关键字
     * @return X Id
     */
    static private int getXId(Channel ch, String key) {
        if (null == ch ||
            null == key) {
            return -1;
        }

        // 获取 Id
        Integer xId = (Integer) ch.attr(AttributeKey.valueOf(key)).get();

        if (null == xId) {
            return -1;
        } else {
            return xId;
        }
    }

    /**
     * 附着会话 Id
     *
     * @param ctx 客户端信道上下文
     */
    static public void attachSessionId(ChannelHandlerContext ctx) {
        if (null != ctx) {
            attachSessionId(ctx.channel());
        }
    }

    /**
     * 附着会话 Id
     *
     * @param ch 信道
     */
    static public void attachSessionId(Channel ch) {
        if (null != ch) {
            putXId(ch, KEY_SESSION_ID, _idGen.incrementAndGet());
        }
    }

    /**
     * 获取会话 Id
     *
     * @param ctx 客户端信道上下文
     * @return 会话 Id
     */
    static public int getSessionId(ChannelHandlerContext ctx) {
        return (null == ctx) ? -1 : getSessionId(ctx.channel());
    }

    /**
     * 获取会话 Id
     *
     * @param ch 客户端信道
     * @return 会话 Id
     */
    static public int getSessionId(Channel ch) {
        return getXId(ch, KEY_SESSION_ID);
    }

    /**
     * 设置用户 Id
     *
     * @param ctx    客户端信道上下文
     * @param userId 用户 Id
     */
    static public void putUserId(ChannelHandlerContext ctx, int userId) {
        if (null != ctx) {
            putUserId(ctx.channel(), userId);
        }
    }

    /**
     * 设置用户 Id
     *
     * @param ch     客户端信道
     * @param userId 用户 Id
     */
    static public void putUserId(Channel ch, int userId) {
        putXId(ch, KEY_USER_ID, userId);
    }

    /**
     * 获取用户 Id
     *
     * @param ctx 客户端信道上下文
     * @return 用户 Id
     */
    static public int getUserId(ChannelHandlerContext ctx) {
        return (null == ctx) ? -1 : getUserId(ctx.channel());
    }

    /**
     * 获取用户 Id
     *
     * @param ch 客户端信道
     * @return 用户 Id
     */
    static public int getUserId(Channel ch) {
        return getXId(ch, KEY_USER_ID);
    }
}
