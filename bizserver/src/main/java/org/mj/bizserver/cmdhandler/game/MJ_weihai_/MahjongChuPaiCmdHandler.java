package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;

/**
 * 麻将出牌指令处理器
 */
public final class MahjongChuPaiCmdHandler
    extends AbstractInGameCmdHandler<MJ_weihai_Protocol.MahjongChuPaiCmd> {

    @Override
    protected void doEasyInvoke(
        int fromUserId, MJ_weihai_Protocol.MahjongChuPaiCmd cmdObj, BizResultWrapper<ReporterTeam> resultX) {
        // 出牌
        MJ_weihai_BizLogic.getInstance().chuPai(
            fromUserId, MahjongTileDef.valueOf(cmdObj.getT()), resultX
        );
    }
}
