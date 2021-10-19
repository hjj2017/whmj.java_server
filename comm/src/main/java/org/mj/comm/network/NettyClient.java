package org.mj.comm.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * Netty 客户端
 */
public final class NettyClient {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 默认的工作线程池
     */
    static private final NioEventLoopGroup DEFAULT_WORKER_GROUP = new NioEventLoopGroup();

    /**
     * 使用配置
     */
    private final NettyClientConf _usingConf;

    /**
     * 客户端信道
     */
    private Channel _ch;

    /**
     * 类参数构造器
     *
     * @param usingConf 使用配置
     * @throws IllegalArgumentException if null == usingConf
     */
    public NettyClient(NettyClientConf usingConf) {
        if (null == usingConf) {
            throw new IllegalArgumentException("usingConf is null");
        }

        _usingConf = usingConf;
    }

    /**
     * 获取使用配置
     *
     * @return 使用配置
     */
    public NettyClientConf getUsingConf() {
        return _usingConf;
    }

    /**
     * 连接
     */
    public void connect() {
        try {
            // Netty NIO 线程池
            NioEventLoopGroup workerGroup = _usingConf.getWorkerGroup();

            if (null == workerGroup) {
                workerGroup = DEFAULT_WORKER_GROUP;
            }

            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    // 获取自定义信道处理器工厂
                    ICustomChannelHandlerFactory f = _usingConf.getCustomChannelHandlerFactory();

                    if (null == f) {
                        return;
                    }

                    // 获取自定义信道处理器
                    ChannelHandler h = f.create();

                    if (null != h) {
                        ch.pipeline().addLast(h);
                    }
                }
            });
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.option(ChannelOption.TCP_NODELAY, true);

            // 客户端开启
            ChannelFuture f = b.connect(
                _usingConf.getServerHost(),
                _usingConf.getServerPort()
            ).sync();

            if (!f.isSuccess()) {
                return;
            }

            _ch = f.channel();
            _ch.closeFuture().addListener(this::onLoseConnect);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 是否已开启
     *
     * @return true = 已开启, false = 未开启
     */
    public boolean isOpen() {
        return null != _ch && _ch.isOpen();
    }

    /**
     * 发送消息
     *
     * @param msgObj 消息对象
     */
    public void sendMsg(Object msgObj) {
        if (null == _ch) {
            LOGGER.error("client channel is null");
            return;
        }

        _ch.writeAndFlush(msgObj);
    }

    /**
     * 当失去连接时
     *
     * @param f 预期
     */
    private void onLoseConnect(Future<?> f) {
        LOGGER.warn(
            "XXX 注意: 服务器连接关闭! {} XXX",
            _usingConf
        );

        _ch = null;

        // 获取并执行关闭回调函数
        final INettyClientCloseCallback callback = _usingConf.getCloseCallback();

        if (null != callback) {
            callback.apply(this);
        }
    }
}
