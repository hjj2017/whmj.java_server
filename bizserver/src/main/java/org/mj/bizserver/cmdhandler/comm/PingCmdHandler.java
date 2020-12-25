package org.mj.bizserver.cmdhandler.comm;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * Ping 指令处理器
 */
public class PingCmdHandler implements ICmdHandler<CommProtocol.PingCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        CommProtocol.PingCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        final InternalServerMsg newMsg = new InternalServerMsg();
        newMsg.setRemoteSessionId(remoteSessionId);
        newMsg.setFromUserId(fromUserId);
        newMsg.putProtoMsg(
            CommProtocol.PingResult.newBuilder()
                .setPingId(cmdObj.getPingId())
                .build()
        );
        
        ctx.writeAndFlush(newMsg);
    }
}
