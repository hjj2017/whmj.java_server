package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 麻将出牌指令处理器
 */
public final class MahjongChuPaiCmdHandler extends AbstractInGameCmdHandler<MJ_weihai_Protocol.MahjongChuPaiCmd>
    implements ICmdHandler<MJ_weihai_Protocol.MahjongChuPaiCmd> {

    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        MJ_weihai_Protocol.MahjongChuPaiCmd cmdObj) {
        // 将复杂工作交给代理, 我只执行省心的调用
        super.doProxyInvoke(ctx, remoteSessionId, fromUserId, cmdObj);
    }

    @Override
    protected void doEasyInvoke(
        int fromUserId, MJ_weihai_Protocol.MahjongChuPaiCmd cmdObj, BizResultWrapper<ReporterTeam> resultX) {
        // 出牌
        MJ_weihai_BizLogic.getInstance().chuPai(
            fromUserId, MahjongTileDef.valueOf(cmdObj.getT()), resultX
        );
    }
}
