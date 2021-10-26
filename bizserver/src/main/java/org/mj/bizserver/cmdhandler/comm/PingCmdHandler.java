package org.mj.bizserver.cmdhandler.comm;

import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * Ping 指令处理器
 */
public class PingCmdHandler implements ICmdHandler<MyCmdHandlerContext, CommProtocol.PingCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx, CommProtocol.PingCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        CommProtocol.PingResult r = CommProtocol.PingResult.newBuilder()
            .setPingId(cmdObj.getPingId())
            .build();

        ctx.writeAndFlush(r);
    }
}
