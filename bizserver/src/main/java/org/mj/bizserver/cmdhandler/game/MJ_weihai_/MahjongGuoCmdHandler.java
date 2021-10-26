package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;

/**
 * 麻将过牌指令处理器
 */
public final class MahjongGuoCmdHandler
    extends AbstractInGameCmdHandler<MJ_weihai_Protocol.MahjongGuoCmd> {

    @Override
    protected void doEasyInvoke(
        int fromUserId, MJ_weihai_Protocol.MahjongGuoCmd cmdObj, BizResultWrapper<ReporterTeam> resultX) {
        // 过牌
        MJ_weihai_BizLogic.getInstance().guo(
            fromUserId, resultX
        );
    }
}
