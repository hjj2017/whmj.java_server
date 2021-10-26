package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;

/**
 * 麻将吃牌指令处理器
 */
public final class MahjongChiCmdHandler
    extends AbstractInGameCmdHandler<MJ_weihai_Protocol.MahjongChiCmd> {

    @Override
    protected void doEasyInvoke(
        int fromUserId, MJ_weihai_Protocol.MahjongChiCmd cmdObj, BizResultWrapper<ReporterTeam> resultX) {
        // 吃牌
        MJ_weihai_BizLogic.getInstance().chi(
            fromUserId,
            cmdObj.getSelectedOption(),
            resultX
        );
    }
}
