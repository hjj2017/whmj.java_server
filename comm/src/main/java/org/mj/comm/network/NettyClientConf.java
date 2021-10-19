package org.mj.comm.network;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Netty 客户端配置
 */
public final class NettyClientConf extends AbstractConf<NettyClientConf> {
    /**
     * 线程池
     */
    private NioEventLoopGroup _workerGroup;

    /**
     * 连接关闭
     */
    private INettyClientCloseCallback _closeCallback;

    /**
     * 获取线程池
     *
     * @return 线程池
     */
    @JSONField(serialize = false, deserialize = false)
    public NioEventLoopGroup getWorkerGroup() {
        return _workerGroup;
    }

    /**
     * 设置线程池
     *
     * @param val 线程池
     * @return this 指针
     */
    public NettyClientConf setWorkerGroup(NioEventLoopGroup val) {
        _workerGroup = val;
        return this;
    }

    /**
     * 获取服务器关闭回调函数
     *
     * @return 服务器关闭回调函数
     */
    @JSONField(serialize = false, deserialize = false)
    public INettyClientCloseCallback getCloseCallback() {
        return _closeCallback;
    }

    /**
     * 设置服务器关闭回调函数
     *
     * @param val 对象值
     * @return this 指针
     */
    public NettyClientConf setCloseCallback(INettyClientCloseCallback val) {
        _closeCallback = val;
        return this;
    }

    /**
     * 从 JSON 对象中创建配置
     *
     * @param jsonObj JSON 对象
     * @return 配置
     */
    static public NettyClientConf fromJSONObj(JSONObject jsonObj) {
        if (null == jsonObj) {
            return null;
        }

        return jsonObj.toJavaObject(NettyClientConf.class);
    }
}
