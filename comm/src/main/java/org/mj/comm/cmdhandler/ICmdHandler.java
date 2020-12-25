package org.mj.comm.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * 命令处理器接口
 */
public interface ICmdHandler<TCmd extends GeneratedMessageV3> {
    /**
     * 处理命令
     *
     * @param ctx             信道上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param cmdObj          命令对象
     */
    void handle(ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, TCmd cmdObj);
}
