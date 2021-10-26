package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;

/**
 * 麻将杠牌指令处理器
 */
public final class MahjongGangCmdHandler
    extends AbstractInGameCmdHandler<MJ_weihai_Protocol.MahjongGangCmd> {

    @Override
    protected void doEasyInvoke(
        int fromUserId, MJ_weihai_Protocol.MahjongGangCmd cmdObj, BizResultWrapper<ReporterTeam> resultX) {
        // 杠牌
        MJ_weihai_BizLogic.getInstance().gang(
            fromUserId, resultX
        );
    }
}
