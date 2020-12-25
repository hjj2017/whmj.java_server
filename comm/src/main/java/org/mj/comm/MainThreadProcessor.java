package org.mj.comm;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.mj.comm.cmdhandler.CmdHandlerFactory;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 住线程处理器
 */
public class MainThreadProcessor {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(MainThreadProcessor.class);

    /**
     * 主线程处理器
     */
    private final CmdHandlerFactory _cmdHandlerFactory;

    /**
     * 线程服务
     */
    private final ExecutorService _es;

    /**
     * 类参数构造器
     *
     * @param cmdHandlerFactory 命令处理器工厂
     * @throws IllegalArgumentException if null == cmdHandlerFactory
     */
    public MainThreadProcessor(String processorName, CmdHandlerFactory cmdHandlerFactory) {
        if (null == cmdHandlerFactory) {
            throw new IllegalArgumentException("cmdHandlerFactory is null");
        }

        _es = Executors.newSingleThreadExecutor((r) -> {
            // 创建线程并起个名字
            Thread t = new Thread(r);
            t.setName(processorName);

            return t;
        });

        _cmdHandlerFactory = cmdHandlerFactory;
    }

    /**
     * 处理消息对象
     *
     * @param ctx             信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param cmdObj          命令对象
     */
    public void process(ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, GeneratedMessageV3 cmdObj) {
        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        this.process(() -> {
            // 获取命令类
            final Class<?> cmdClazz = cmdObj.getClass();
            // 创建命令处理器
            final ICmdHandler<? extends GeneratedMessageV3> cmdHandler = _cmdHandlerFactory.create(cmdClazz);

            if (null == cmdHandler) {
                LOGGER.error(
                    "未找到命令处理器, cmdClazz = {}",
                    cmdClazz
                );
                return;
            }

            LOGGER.debug(
                "处理命令, cmdClazz = {}",
                cmdClazz.getName()
            );

            cmdHandler.handle(
                ctx, remoteSessionId, fromUserId, cast(cmdObj)
            );
        });
    }

    /**
     * 处理任务
     *
     * @param task 任务对象
     */
    public void process(Runnable task) {
        if (null != task) {
            _es.submit(new SafeRunner(task));
        }
    }

    /**
     * 转型消息对象
     *
     * @param msgObj 消息对象
     * @param <TCmd> 命令类型
     * @return 转型后的消息对象
     */
    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msgObj) {
        @SuppressWarnings("unchecked")
        TCmd tempObj = (TCmd) msgObj;
        return tempObj;
    }

    /**
     * 安全运行
     */
    static private class SafeRunner implements Runnable {
        /**
         * 内置运行实例
         */
        private final Runnable _innerR;

        /**
         * 类参数构造器
         *
         * @param innerR 内置运行实例
         */
        SafeRunner(Runnable innerR) {
            _innerR = innerR;
        }

        @Override
        public void run() {
            if (null == _innerR) {
                return;
            }

            try {
                // 运行
                _innerR.run();
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
