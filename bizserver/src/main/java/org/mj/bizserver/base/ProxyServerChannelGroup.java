package org.mj.bizserver.base;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理服务器信道分
 */
public final class ProxyServerChannelGroup {
    /**
     * 信道字典, key = proxyServerId
     */
    static private final Map<Integer, Channel> _chMap = new ConcurrentHashMap<>();

    /**
     * 私有化类默认构造器
     */
    private ProxyServerChannelGroup() {
    }

    /**
     * 根据上下文添加代理服务器信道
     *
     * @param ctx 代理服务器信道上下文
     */
    static public void add(ChannelHandlerContext ctx) {
        if (null != ctx) {
            add(ctx.channel());
        }
    }

    /**
     * 添加代理服务器信道
     *
     * @param ch 代理服务器信道
     */
    static public void add(Channel ch) {
        if (null == ch) {
            return;
        }

        int sessionId = IdSetterGetter.getProxyServerId(ch);
        _chMap.put(sessionId, ch);
    }

    /**
     * 根据上下文移除代理服务器信道
     *
     * @param ctx 代理服务器信道上下文
     */
    static public void remove(ChannelHandlerContext ctx) {
        if (null != ctx) {
            remove(ctx.channel());
        }
    }

    /**
     * 移除代理服务器信道
     *
     * @param ch 代理服务器信道
     */
    static public void remove(Channel ch) {
        if (null == ch) {
            return;
        }

        _chMap.values().remove(ch);
    }

    /**
     * 根据代理服务器 Id 获取代理服务器信道
     *
     * @param proxyServerId 代理服务器 Id
     * @return 代理服务器信道
     */
    static public Channel getByProxyServerId(int proxyServerId) {
        if (proxyServerId <= 0) {
            return null;
        } else {
            return _chMap.get(proxyServerId);
        }
    }
}
