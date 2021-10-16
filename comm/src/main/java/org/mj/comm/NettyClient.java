package org.mj.comm;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Netty 客户端
 */
public final class NettyClient {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 默认线程池
     */
    static private final NioEventLoopGroup DEFAULT_WORKER_GROUP = new NioEventLoopGroup();

    /**
     * 使用配置
     */
    private final Config _usingConf;

    /**
     * 额外信息字典
     */
    private Map<String, String> _extraInfoMap;

    /**
     * 客户端信道
     */
    private Channel _ch;

    /**
     * 已就绪
     */
    private boolean _ready = false;

    /**
     * 类参数构造器
     *
     * @param usingConf 使用配置
     * @throws IllegalArgumentException if null == usingConf
     */
    public NettyClient(Config usingConf) {
        if (null == usingConf) {
            throw new IllegalArgumentException("usingConf is null");
        }

        _usingConf = usingConf;
    }

    /**
     * 设置附加信息
     *
     * @param key 关键字
     * @param val 字符串值
     */
    public void putExtraInfo(String key, String val) {
        if (null == key) {
            return;
        }

        if (null == val &&
            null == _extraInfoMap) {
            return;
        }

        if (null == _extraInfoMap) {
            _extraInfoMap = new ConcurrentHashMap<>();
        }

        _extraInfoMap.put(key, val);
    }

    /**
     * 获取服务器 Id
     *
     * @return 服务器 Id
     */
    public int getServerId() {
        return _usingConf.getServerId();
    }

    /**
     * 获取服务器名称
     *
     * @return 服务器名称
     */
    public String getServerName() {
        return _usingConf.getServerName();
    }

    /**
     * 获取服务器工作类型数组
     *
     * @return 服务器工作类型数组
     */
    public Set<String> getServerJobTypeSet() {
        return _usingConf.getServerJobTypeSet();
    }

    /**
     * 获取服务器主机地址
     *
     * @return 服务器主机地址
     */
    public String getServerHost() {
        return _usingConf.getServerHost();
    }

    /**
     * 获取服务器端口号
     *
     * @return 服务器端口号
     */
    public int getServerPort() {
        return _usingConf.getServerPort();
    }

    /**
     * 是否准备好
     *
     * @return true = 已准备好, false = 未准备好
     */
    public boolean isReady() {
        return _ready;
    }

    /**
     * 连接
     */
    public void conn() {
        try {
            final URI serverURI = new URI(MessageFormat.format(
                "ws://{0}:{1}/websocket",
                _usingConf.getServerHost(),
                String.valueOf(_usingConf.getServerPort())
            ));

            final DefaultHttpHeaders headerz = new DefaultHttpHeaders();

            if (null != _extraInfoMap) {
                for (Map.Entry<String, String> entry : _extraInfoMap.entrySet()) {
                    if (null != entry.getValue()) {
                        headerz.add(
                            entry.getKey(), entry.getValue()
                        );
                    }
                }
            }

            final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                serverURI,
                WebSocketVersion.V13,
                null,
                true,
                headerz
            );

            Bootstrap b = new Bootstrap();
            b.group(DEFAULT_WORKER_GROUP);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    // 获取信道处理器工厂
                    final Config.AbstractChannelHandlerFactory f = _usingConf.getChannelHandlerFactory();
                    // 消息处理器
                    ChannelHandler msgHandler = null;

                    if (null != f) {
                        msgHandler = f.createMsgHandler();
                    }

                    ChannelHandler[] hArray = {
                        new HttpClientCodec(), // Http 客户端编解码器
                        new HttpObjectAggregator(65535), // 内容长度限制
                        new WebSocketClientProtocolHandler(handshaker), // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                        msgHandler, // 消息处理器
                    };

                    for (ChannelHandler h : hArray) {
                        if (null != h) {
                            ch.pipeline().addLast(h);
                        }
                    }
                }
            });
            b.option(ChannelOption.SO_KEEPALIVE, true);

            // 客户端开启
            ChannelFuture f = b.connect(
                _usingConf.getServerHost(),
                _usingConf.getServerPort()
            ).sync();

            if (!f.isSuccess()) {
                return;
            }

            _ch = f.channel();
            _ch.closeFuture().addListener((x) -> {
                // 获取已关闭的客户端
                NettyClient closeClient = NettyClient.this;

                LOGGER.warn(
                    "XXX 注意: 服务器连接关闭! {} XXX",
                    _usingConf
                );

                _ready = false;
                closeClient._ch = null;

                // 获取并执行关闭回调函数
                final ICloseCallback callback = _usingConf.getCloseCallback();

                if (null != callback) {
                    callback.apply(closeClient);
                }
            });

            // 用 CD 来等待握手
            final CountDownLatch cdL = new CountDownLatch(32);

            while (cdL.getCount() > 0 &&
                !handshaker.isHandshakeComplete()) {
                // 在这里等待握手成功
                cdL.await(200, TimeUnit.MILLISECONDS);
                cdL.countDown();
            }

            if (!handshaker.isHandshakeComplete()) {
                return;
            }

            LOGGER.info(
                ">>> 连接到服务器成功! {} <<<",
                _usingConf
            );

            // 准备完成
            _ready = true;
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 发送消息
     *
     * @param msgObj 消息对象
     */
    public void sendMsg(Object msgObj) {
        if (!isReady()) {
            LOGGER.error("客户端未准备好");
            return;
        }

        if (null == _ch) {
            LOGGER.error("client channel is null");
            return;
        }

        _ch.writeAndFlush(msgObj);
    }

    /**
     * 配置
     */
    static public final class Config {
        /**
         * 服务器 Id
         */
        private int _serverId;

        /**
         * 服务器名称
         */
        private String _serverName;

        /**
         * 服务器工作类型集合
         */
        private Set<String> _serverJobTypeSet;

        /**
         * 服务器地址
         */
        private String _serverHost;

        /**
         * 服务器端口号
         */

        private int _serverPort;

        /**
         * 信道处理器工厂
         */
        private AbstractChannelHandlerFactory _channelHandlerFactory;

        /**
         * 连接关闭
         */
        private ICloseCallback _closeCallback;

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
         * 设置服务器名称
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
         * @param val 字符串集合
         */
        public void setServerJobTypeSet(Set<String> val) {
            _serverJobTypeSet = val;
        }

        /**
         * 获取服务器地址
         *
         * @return 服务器地址
         */
        @JSONField(name = "serverHost")
        public String getServerHost() {
            return _serverHost;
        }

        /**
         * 设置服务器地址
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
         * 获取信道处理器工厂
         *
         * @return 信道处理器工厂
         */
        @JSONField(serialize = false, deserialize = false)
        public AbstractChannelHandlerFactory getChannelHandlerFactory() {
            return _channelHandlerFactory;
        }

        /**
         * 设置信道处理器工厂
         *
         * @param val 对象值
         */
        public void setChannelHandlerFactory(AbstractChannelHandlerFactory val) {
            _channelHandlerFactory = val;
        }

        /**
         * 获取服务器关闭回调函数
         *
         * @return 服务器关闭回调函数
         */
        @JSONField(serialize = false, deserialize = false)
        public ICloseCallback getCloseCallback() {
            return _closeCallback;
        }

        /**
         * 设置服务器关闭回调函数
         *
         * @param val 对象值
         */
        public void setCloseCallback(ICloseCallback val) {
            _closeCallback = val;
        }

        @Override
        public String toString() {
            return MessageFormat.format(
                "serverId = {0}, serverName = {1}, serverJobType = {2}, addr = {3}:{4}",
                String.valueOf(_serverId),
                _serverName,
                _serverJobTypeSet,
                _serverHost,
                String.valueOf(_serverPort)
            );
        }

        /**
         * 从 JSON 对象中创建配置
         *
         * @param jsonObj JSON 对象
         * @return 配置
         */
        static public Config fromJSONObj(JSONObject jsonObj) {
            if (null == jsonObj) {
                return null;
            }

            return jsonObj.toJavaObject(Config.class);
        }

        /**
         * 抽象的信道处理器工厂
         */
        static public abstract class AbstractChannelHandlerFactory {
            /**
             * 创建消息处理器
             *
             * @return 消息处理器
             */
            public abstract ChannelHandler createMsgHandler();
        }
    }

    /**
     * 关闭回调接口
     */
    public interface ICloseCallback {
        /**
         * 执行回调
         *
         * @param closeClient 关闭客户端
         */
        void apply(NettyClient closeClient);
    }
}
