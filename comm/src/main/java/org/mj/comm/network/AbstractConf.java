package org.mj.comm.network;

import com.alibaba.fastjson.annotation.JSONField;

import java.text.MessageFormat;

/**
 * 抽象的配置
 */
abstract class AbstractConf<T extends AbstractConf<T>> {
    /**
     * 服务器 Id
     */
    private String _serverId;

    /**
     * 服务器地址
     */
    private String _serverHost;

    /**
     * 服务器端口号
     */
    private int _serverPort;

    /**
     * 自定义信道处理器工厂
     */
    private ICustomChannelHandlerFactory _customChannelHandlerFactory;

    /**
     * 获取服务器 Id
     *
     * @return 服务器 Id
     */
    @JSONField(name = "serverId")
    public String getServerId() {
        return _serverId;
    }

    /**
     * 设置服务器 Id
     *
     * @param val 服务器 Id
     * @return this 指针
     */
    public T setServerId(String val) {
        _serverId = val;
        return SELF();
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
     * @return this 指针
     */
    public T setServerHost(String val) {
        _serverHost = val;
        return SELF();
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
     * @param val 服务器端口号
     * @return this 指针
     */
    public T setServerPort(int val) {
        _serverPort = val;
        return SELF();
    }

    /**
     * 获取自定义信道处理器工厂
     *
     * @return 自定义信道处理器工厂
     */
    public ICustomChannelHandlerFactory getCustomChannelHandlerFactory() {
        return _customChannelHandlerFactory;
    }

    /**
     * 设置自定义信道处理器工厂
     *
     * @param val 自定义信道处理器工厂
     * @return this 指针
     */
    public T setCustomChannelHandlerFactory(ICustomChannelHandlerFactory val) {
        _customChannelHandlerFactory = val;
        return SELF();
    }

    /**
     * 获取转型后的 this 指针
     *
     * @return this 指针
     */
    private T SELF() {
        @SuppressWarnings("unchecked")
        T t = (T) this;
        return t;
    }

    @Override
    public String toString() {
        return MessageFormat.format(
            "serverId = {0}, addrezz = {1}:{2}",
            getServerId(), getServerHost(), String.valueOf(getServerPort())
        );
    }
}
