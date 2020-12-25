package org.mj.bizserver.foundation;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.mj.comm.MainThreadProcessor;
import org.mj.comm.cmdhandler.CmdHandlerFactory;

/**
 * 主线程处理器单例
 */
public final class MainThreadProcessorSingleton {
    /**
     * 单例对象
     */
    static private final MainThreadProcessorSingleton _instance = new MainThreadProcessorSingleton();

    /**
     * 主线程处理器
     */
    private final MainThreadProcessor _mainTP;

    /**
     * 类默认构造器
     */
    private MainThreadProcessorSingleton() {
        _mainTP = new MainThreadProcessor(
            "bizServer-mainThreadProcessor",
            new CmdHandlerFactory("org.mj.bizserver.cmdhandler")
        );
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public MainThreadProcessorSingleton getInstance() {
        return _instance;
    }

    /**
     * 处理消息对象
     *
     * @param ctx             信道处理器上下文
     * @param remoteSessionId 远程回话 Id
     * @param fromUserId      来自用户 Id
     * @param cmdObj          命令对象
     */
    public void process(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, GeneratedMessageV3 cmdObj) {
        // 处理消息对象
        _mainTP.process(ctx, remoteSessionId, fromUserId, cmdObj);
    }

    /**
     * 处理任务
     *
     * @param task 任务对象
     */
    public void process(Runnable task) {
        _mainTP.process(task);
    }

    /**
     * 获取真正的主线程处理器
     *
     * @return 主线程处理器
     */
    MainThreadProcessor getActualMainTP() {
        return _mainTP;
    }
}
