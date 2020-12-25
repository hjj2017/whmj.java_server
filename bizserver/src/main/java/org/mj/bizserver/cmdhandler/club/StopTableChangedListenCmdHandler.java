package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 停止牌桌变化监听命令处理器
 */
public class StopTableChangedListenCmdHandler
    implements ICmdHandler<ClubServerProtocol.StopTableChangedListenCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.StopTableChangedListenCmd cmdObj) {
    }
}
