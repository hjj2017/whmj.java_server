package org.mj.bizserver.cmdhandler.club;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.club.adminctrl.AdminCtrlBizLogic;
import org.mj.bizserver.mod.club.membercenter.bizdata.FixGameX;
import org.mj.comm.cmdhandler.ICmdHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 修改固定玩法指令处理器
 */
public class ModifyFixGameXCmdHandler implements ICmdHandler<ClubServerProtocol.ModifyFixGameCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ClubServerProtocol.ModifyFixGameCmd cmdObj) {

        final GameType0Enum gameType0 = GameType0Enum.valueOf(cmdObj.getGameType0());
        final GameType1Enum gameType1 = GameType1Enum.valueOf(cmdObj.getGameType1());

        final FixGameX fixGameX = new FixGameX();
        fixGameX.setIndex(cmdObj.getIndex());
        fixGameX.setGameType0(gameType0);
        fixGameX.setGameType1(gameType1);

        // 获取规则条目列表
        List<ClubServerProtocol.KeyAndVal> ruleItemList = cmdObj.getRuleItemList();

        if (null != ruleItemList &&
            !ruleItemList.isEmpty()) {
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
            fromUserId, cmdObj.getClubId(), fixGameX,
            (resultX) -> buildResultMsgAndSend(
                ctx, remoteSessionId, fromUserId, cmdObj.getClubId(), fixGameX, resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param clubId          亲友圈 Id
     * @param fixGameX        固定玩法
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, int clubId, final FixGameX fixGameX,
        BizResultWrapper<Boolean> resultX) {
        if (null == ctx ||
            null == fixGameX ||
            null == resultX) {
            return;
        }

        final InternalServerMsg newMsg = new InternalServerMsg();
        newMsg.setRemoteSessionId(remoteSessionId);
        newMsg.setFromUserId(fromUserId);

        if (0 != newMsg.admitError(resultX)) {
            ctx.writeAndFlush(newMsg);
            return;
        }

        ClubServerProtocol.ModifyFixGameResult r = ClubServerProtocol.ModifyFixGameResult.newBuilder()
            .setClubId(clubId)
            .setIndex(fixGameX.getIndex())
            .setSuccezz(resultX.getFinalResult())
            .build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
