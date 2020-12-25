package org.mj.comm;

import com.alibaba.fastjson.annotation.JSONField;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Netty 服务器
 */
public final class NettyServer {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 使用配置
     */
    private final Config _usingConf;

    /**
     * 类参数构造器
     *
     * @param usingConf 使用配置
     * @throws IllegalArgumentException if null == usingConf
     */
    public NettyServer(Config usingConf) {
        if (null == usingConf) {
            throw new IllegalArgumentException("usingConf is null");
        }

        _usingConf = usingConf;
    }

    /**
     * 启动服务器
     */
    public void startUp() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();   // 拉客的
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(); // 干活的

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class); // 服务器信道的处理方式
        b.childHandler(new ChannelInitializer<SocketChannel>() {
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
                    new HttpServerCodec(), // Http 服务器编解码器
                    new HttpObjectAggregator(65535), // 内容长度限制
                    new WebSocketServerProtocolHandler("/websocket"), // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                    msgHandler, // 消息处理器
                };

                for (ChannelHandler h : hArray) {
                    if (null != h) {
                        ch.pipeline().addLast(h);
                    }
                }
            }
        });
        b.option(ChannelOption.SO_BACKLOG, 128);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            // 绑定指定端口,
            // 具体使用哪个端口是由 argvArray 中的参数来声明
            ChannelFuture f = b.bind(
                _usingConf.getServerHost(),
                _usingConf.getServerPort()
            ).sync();

            if (f.isSuccess()) {
                LOGGER.info(
                    ">>> 服务器启动成功! {} <<<",
                    _usingConf
                );
            }
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
         * 信道处理器工厂
         */
        private AbstractChannelHandlerFactory _channelHandlerFactory;

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
}
