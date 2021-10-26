package org.mj.bizserver.base;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * Id 设置器和获取器
 */
public class IdSetterGetter {
    /**
     * 代理服务器 Id
     */
    static private final String KEY_PROXY_SERVER_ID = "mj_proxy_server_id";

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
     * 获取代理服务器 Id
     *
     * @param ctx 代理服务器信道上下文
     * @return 会话 Id
     */
    static public int getProxyServerId(ChannelHandlerContext ctx) {
        return (null == ctx) ? -1 : getProxyServerId(
            ctx.channel()
        );
    }

    /**
     * 获取代理服务器 Id
     *
     * @param ch 代理服务器信道
     * @return 代理服务器 Id
     */
    static public int getProxyServerId(Channel ch) {
        return getXId(ch, KEY_PROXY_SERVER_ID);
    }

    /**
     * 设置代理服务器 Id
     *
     * @param ctx           代理服务器信道上下文
     * @param proxyServerId 代理服务器 Id
     */
    static public void putProxyServerId(ChannelHandlerContext ctx, int proxyServerId) {
        putProxyServerId(ctx.channel(), proxyServerId);
    }

    /**
     * 设置代理服务器 Id
     *
     * @param ch            代理服务器信道
     * @param proxyServerId 代理服务器 Id
     */
    static public void putProxyServerId(
        Channel ch,
        int proxyServerId) {
        putXId(ch, KEY_PROXY_SERVER_ID, proxyServerId);
    }
}
