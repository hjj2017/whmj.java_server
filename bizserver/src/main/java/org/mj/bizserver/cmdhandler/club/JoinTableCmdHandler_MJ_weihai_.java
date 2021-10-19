package org.mj.bizserver.cmdhandler.club;

import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.cmdhandler.game.MJ_weihai_.GameBroadcaster;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * 加入牌桌指令处理器 - 威海麻将
 */
class JoinTableCmdHandler_MJ_weihai_ {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(JoinTableCmdHandler_MJ_weihai_.class);

    /**
     * 处理消息指令
     *
     * @param ctx    信道处理器上下文
     * @param cmdObj 指令对象
     */
    static void handle(
        MyCmdHandlerContext ctx, ClubServerProtocol.JoinTableCmd cmdObj) {

        // 获取房间 Id
        final int roomId = cmdObj.getRoomId();

        if (!MJ_weihai_BizLogic.getInstance().hasRoom(roomId)) {
            LOGGER.error(
                "房间 Id 不存在, userId = {}, roomId = {}",
                ctx.getFromUserId(), roomId
            );
            return;
        }

        MJ_weihai_BizLogic.getInstance().joinRoom_async(
            ctx.getFromUserId(),
            roomId,
            (resultX) -> buildMsgAndSend(ctx, resultX)
        );
    }

    /**
     * 构建消息并发送
     *
     * @param ctx     客户端信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildMsgAndSend(
        MyCmdHandlerContext ctx, BizResultWrapper<Room> resultX) {
        // 构建结果并发送
        buildResultAndSend(
            ctx, resultX
        );
        // 构建广播并发送
        buildBroadcastAndSend(
            ctx, resultX
        );
    }

    /**
     * 构建结果并发送
     *
     * @param ctx     信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildResultAndSend(
        MyCmdHandlerContext ctx, BizResultWrapper<Room> resultX) {
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

        // 添加到广播器
        GameBroadcaster.add(
            ctx.getNettyChannel(), ctx.getRemoteSessionId(), ctx.getFromUserId()
        );

        // 获取加入房间
        Room joinedRoom = resultX.getFinalResult();

        if (null == joinedRoom) {
            LOGGER.error(
                "加入房间为空, userId = {}",
                ctx.getFromUserId()
            );
            return;
        }

        // 构建加入牌桌结果
        ClubServerProtocol.JoinTableResult.Builder b0 = ClubServerProtocol.JoinTableResult.newBuilder();
        b0.setRoomId(joinedRoom.getRoomId());
        b0.setGameType0(joinedRoom.getGameType0().getIntVal());
        b0.setGameType1(joinedRoom.getGameType1().getIntVal());

        // 获取规则字典
        final Map<Integer, Integer> ruleMap = joinedRoom.getRuleSetting().getInnerMap();

        for (Map.Entry<Integer, Integer> entry : ruleMap.entrySet()) {
            b0.addRuleItem(
                ClubServerProtocol.KeyAndVal.newBuilder()
                    .setKey(entry.getKey())
                    .setVal(entry.getValue())
            );
        }

        ctx.writeAndFlush(b0.build());
    }

    /**
     * 构建广播消息
     *
     * @param ctx     信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildBroadcastAndSend(
        MyCmdHandlerContext ctx, BizResultWrapper<Room> resultX) {
        if (null == ctx ||
            null == resultX ||
            0 != resultX.getErrorCode()) {
            return;
        }

        // 获取加入房间
        Room joinedRoom = resultX.getFinalResult();

        if (null == joinedRoom) {
            LOGGER.error(
                "加入房间为空, userId = {}",
                ctx.getFromUserId()
            );
            return;
        }

        // 获取房间内的玩家
        Player p = joinedRoom.getPlayerByUserId(ctx.getFromUserId());

        if (null == p) {
            LOGGER.error(
                "玩家没有加入该房间, userId = {}, roomId = {}",
                ctx.getFromUserId(),
                joinedRoom.getRoomId()
            );
            return;
        }

        // 构建加入房间广播
        MJ_weihai_Protocol.JoinRoomBroadcast.Builder b = MJ_weihai_Protocol.JoinRoomBroadcast.newBuilder()
            .setUserId(p.getUserId())
            .setUserName(Objects.requireNonNullElse(p.getUserName(), ""))
            .setHeadImg(Objects.requireNonNullElse(p.getHeadImg(), ""))
            .setSex(p.getSex())
            .setClientIpAddr(Objects.requireNonNullElse(p.getClientIpAddr(), ""))
            .setSeatIndex(p.getSeatIndex())
            .setCurrScore(p.getCurrScore())
            .setTotalScore(p.getTotalScore());

        MJ_weihai_Protocol.JoinRoomBroadcast broadcast = b.build();

        // 广播消息
        GameBroadcaster.broadcast(
            joinedRoom,
            broadcast
        );
    }
}
