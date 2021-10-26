package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 停止牌桌变化监听命令处理器
 */
public class StopTableChangedListenCmdHandler
    implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.StopTableChangedListenCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.StopTableChangedListenCmd cmdObj) {
    }
}
