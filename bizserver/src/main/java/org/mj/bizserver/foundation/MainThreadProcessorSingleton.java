package org.mj.bizserver.foundation;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.comm.MainThreadProcessor;
import org.mj.comm.cmdhandler.CmdHandlerFactory;

/**
 * 主线程处理器单例
 */
public final class MainThreadProcessorSingleton {
    /**
     * 单例对象
     */
    static private final MainThreadProcessorSingleton INSTANCE = new MainThreadProcessorSingleton();

    /**
     * 主线程处理器
     */
    private final MainThreadProcessor _innerProcessor = new MainThreadProcessor(
        "bizServer_mainThreadProcessor",
        new CmdHandlerFactory("org.mj.bizserver.cmdhandler")
    );
    ;

    /**
     * 私有化类默认构造器
     */
    private MainThreadProcessorSingleton() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public MainThreadProcessorSingleton getInstance() {
        return INSTANCE;
    }

    /**
     * 处理消息对象
     *
     * @param ctx    命令处理器上下文
     * @param cmdObj 命令对象
     */
    public void process(MyCmdHandlerContext ctx, GeneratedMessageV3 cmdObj) {
        _innerProcessor.process(ctx, cmdObj);
    }

    /**
     * 处理任务
     *
     * @param task 任务对象
     */
    public void process(Runnable task) {
        _innerProcessor.process(task);
    }
}
