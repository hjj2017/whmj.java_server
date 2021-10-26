package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.FixGameX;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改固定玩法指令处理器
 */
public class ModifyFixGameXCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.ModifyFixGameCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.ModifyFixGameCmd cmdObj) {

        final GameType0Enum gameType0 = GameType0Enum.valueOf(cmdObj.getGameType0());
        final GameType1Enum gameType1 = GameType1Enum.valueOf(cmdObj.getGameType1());

        final FixGameX fixGameX = new FixGameX();
        fixGameX.setIndex(cmdObj.getIndex());
        fixGameX.setGameType0(gameType0);
        fixGameX.setGameType1(gameType1);

        // 获取规则条目列表
        List<ClubServerProtocol.KeyAndVal> ruleItemList = cmdObj.getRuleItemList();

        if (!ruleItemList.isEmpty()) {
            Map<Integer, Integer> ruleMap = new HashMap<>();

            for (ClubServerProtocol.KeyAndVal keyAndVal : ruleItemList) {
                ruleMap.put(
                    keyAndVal.getKey(),
                    keyAndVal.getVal()
                );
            }

            fixGameX.setRuleMap(ruleMap);
        }

        AdminCtrlBizLogic.getInstance().modifyFixGameX_async(
            ctx.getFromUserId(),
            cmdObj.getClubId(),
            fixGameX,
            (resultX) -> buildResultMsgAndSend(
                ctx, cmdObj.getClubId(), fixGameX, resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx      客户端信道处理器上下文
     * @param clubId   亲友圈 Id
     * @param fixGameX 固定玩法
     * @param resultX  业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, int clubId, final FixGameX fixGameX,
        BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
            null == fixGameX ||
            null == resultX) {
            return;
        }

        if (0 != resultX.getErrorCode()) {
            ctx.sendError(
                resultX.getErrorCode(), resultX.getErrorMsg()
            );
            return;
        }

        ClubServerProtocol.ModifyFixGameResult r = ClubServerProtocol.ModifyFixGameResult.newBuilder()
            .setClubId(clubId)
            .setIndex(fixGameX.getIndex())
            .setSuccezz(resultX.getFinalResult())
            .build();

        ctx.writeAndFlush(r);
    }
}
