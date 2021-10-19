package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;

/**
 * 碰牌指令处理器
 */
public final class MahjongPengCmdHandler
    extends AbstractInGameCmdHandler<MJ_weihai_Protocol.MahjongPengCmd> {

    @Override
    protected void doEasyInvoke(
        int fromUserId, MJ_weihai_Protocol.MahjongPengCmd cmdObj, BizResultWrapper<ReporterTeam> resultX) {
        // 碰牌
        MJ_weihai_BizLogic.getInstance().peng(
            fromUserId, resultX
        );
    }
}
