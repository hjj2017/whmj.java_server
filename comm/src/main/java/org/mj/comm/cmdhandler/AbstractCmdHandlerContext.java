package org.mj.comm.cmdhandler;

import io.netty.channel.ChannelFuture;

/**
 * 自定义指令处理器上下文
 */
abstract public class AbstractCmdHandlerContext {
    /**
     * 代理服务器 Id
     */
    private String _proxyServerId = null;

    /**
     * 远程会话 Id
     */
    private int _remoteSessionId = -1;

    /**
     * 客户端 IP
     */
    private String _clientIP = null;

    /**
     * 来自用户 Id
     */
    private long _fromUserId = -1;

    /**
     * 获取代理服务器 Id
     *
     * @return 代理服务器 Id
     */
    public String getProxyServerId() {
        return _proxyServerId;
    }

    /**
     * 设置代理服务器 Id
     *
     * @param val 代理服务器 Id
     * @return this 指针
     */
    public AbstractCmdHandlerContext setProxyServerId(String val) {
        _proxyServerId = val;
        return this;
    }

    /**
     * 获取远程会话 Id
     *
     * @return 远程会话 Id
     */
    public int getRemoteSessionId() {
        return _remoteSessionId;
    }

    /**
     * 设置远程会话 Id
     *
     * @param val 远程会话 Id
     * @return this 指针
     */
    public AbstractCmdHandlerContext setRemoteSessionId(int val) {
        _remoteSessionId = val;
        return this;
    }

    /**
     * 获取客户端 IP
     *
     * @return 客户端 IP
     */
    public String getClientIP() {
        return _clientIP;
    }

    /**
     * 设置客户端 IP
     *
     * @param val 客户端 IP
     * @return this 指针
     */
    public AbstractCmdHandlerContext setClientIP(String val) {
        _clientIP = val;
        return this;
    }

    /**
     * 获取来自用户 Id
     *
     * @return 来自用户 Id
     */
    public long getFromUserId() {
        return _fromUserId;
    }

    /**
     * 设置来自用户 Id
     *
     * @param val 来自用户 Id
     * @return this 指针
     */
    public AbstractCmdHandlerContext setFromUserId(long val) {
        _fromUserId = val;
        return this;
    }

    /**
     * 写出消息
     *
     * @param msgObj 消息对象
     * @return 信道预期
     */
    abstract public ChannelFuture writeAndFlush(Object msgObj);
}
