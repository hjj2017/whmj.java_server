package org.mj.comm.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;

/**
 * 命令处理器接口
 */
public interface ICmdHandler<TCtx extends AbstractCmdHandlerContext, TCmd extends GeneratedMessageV3> {
    /**
     * 处理命令
     *
     * @param ctx    命令处理器上下文
     * @param cmdObj 命令对象
     */
    void handle(TCtx ctx, TCmd cmdObj);
}
