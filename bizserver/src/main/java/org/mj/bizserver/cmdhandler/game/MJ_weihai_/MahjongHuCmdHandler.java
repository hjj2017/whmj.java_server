package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;

/**
 * 麻将胡牌
 */
public final class MahjongHuCmdHandler
    extends AbstractInGameCmdHandler<MJ_weihai_Protocol.MahjongHuCmd> {

    @Override
    protected void doEasyInvoke(
        int fromUserId, MJ_weihai_Protocol.MahjongHuCmd cmdObj, BizResultWrapper<ReporterTeam> resultX) {
        // 麻将胡牌
        MJ_weihai_BizLogic.getInstance().hu(
            fromUserId, resultX
        );
    }
}
