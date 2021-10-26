package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 开始牌桌变化监听命令处理器
 */
public class StartTableChangedListenCmdHandler
    implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.StartTableChangedListenCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.StartTableChangedListenCmd cmdObj) {
    }
}
