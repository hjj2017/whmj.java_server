package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 开始牌桌变化监听命令处理器
 */
public class StartTableChangedListenCmdHandler
    implements ICmdHandler<ClubServerProtocol.StartTableChangedListenCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.StartTableChangedListenCmd cmdObj) {
    }
}
