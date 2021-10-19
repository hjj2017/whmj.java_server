package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建牌桌指令处理器
 * <p>
 * XXX 注意: 该指令处理器将运行在游戏服务器进程中,
 * 因为 ProxyServer 在接到创建房间指令之后做了一个跳转操作!
 * 直接将 CreateTableCmd 转发给游戏服务器...
 * 虽然看上去还是亲友圈的代码,
 * 但真正的运行进程是在游戏服务器上!
 */
public class CreateTableCmdHandler implements ICmdHandler<MyCmdHandlerContext, ClubServerProtocol.CreateTableCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(CreateTableCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ClubServerProtocol.CreateTableCmd cmdObj) {
        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        Map<Integer, Integer> ruleMap = new ConcurrentHashMap<>(cmdObj.getRuleItemCount());

        for (int i = 0; i < cmdObj.getRuleItemCount(); i++) {
            ClubServerProtocol.KeyAndVal ruleItem = cmdObj.getRuleItem(i);

            ruleMap.putIfAbsent(
                ruleItem.getKey(), ruleItem.getVal()
            );
        }

        // 获取游戏类型 0 和游戏类型 1
        GameType0Enum gameType0 = GameType0Enum.valueOf(cmdObj.getGameType0());
        GameType1Enum gameType1 = GameType1Enum.valueOf(cmdObj.getGameType1());

        if (GameType0Enum.MAHJONG == gameType0 &&
            GameType1Enum.MJ_weihai_ == gameType1) {
            // 威海麻将
            MJ_weihai_BizLogic.getInstance().createRoom_async(
                ctx.getFromUserId(),
                cmdObj.getClubId(),
                cmdObj.getSeqNum(),
                cmdObj.getUsingFixGameX(),
                ruleMap,
                (resultX) -> buildResultMsgAndSend(ctx, resultX)
            );
        } else {
            LOGGER.error(
                "未知游戏类型, userId = {}, gameType0 = {}, gameType1 = {}",
                ctx.getFromUserId(),
                cmdObj.getGameType0(),
                cmdObj.getGameType1()
            );
        }
    }

    /**
     * 构建消息并发送
     *
     * @param ctx     客户端信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, BizResultWrapper<Integer> resultX) {
        if (null == ctx ||
            null == resultX) {
            return;
        }

        if (0 != resultX.getErrorCode()) {
            ctx.sendError(
                resultX.getErrorCode(), resultX.getErrorMsg()
            );
            return;
        }

        // 新房间 Id
        final int newRoomId = resultX.getFinalResult();

        ClubServerProtocol.CreateTableResult.Builder b = ClubServerProtocol.CreateTableResult.newBuilder();
        b.setRoomId(newRoomId);

        ClubServerProtocol.CreateTableResult r = b.build();

        ctx.writeAndFlush(r);
    }
}
