package org.mj.comm.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final NettyServerConf _usingConf;

    /**
     * 类参数构造器
     *
     * @param usingConf 使用配置
     * @throws IllegalArgumentException if null == usingConf
     */
    public NettyServer(NettyServerConf usingConf) {
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
    public NettyServerConf getUsingConf() {
        return _usingConf;
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
        b.option(ChannelOption.SO_BACKLOG, 128);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        b.childOption(ChannelOption.TCP_NODELAY, true);

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
}
