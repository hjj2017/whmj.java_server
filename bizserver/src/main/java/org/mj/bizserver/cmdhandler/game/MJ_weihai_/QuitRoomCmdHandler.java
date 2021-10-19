package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 退出房间指令处理器
 */
public class QuitRoomCmdHandler implements ICmdHandler<MyCmdHandlerContext, MJ_weihai_Protocol.QuitRoomCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(QuitRoomCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        MJ_weihai_Protocol.QuitRoomCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        Room currRoom = RoomGroup.getByUserId(ctx.getFromUserId());

        if (null == currRoom) {
            LOGGER.error(
                "房间为空, fromUserId = {}",
                ctx.getFromUserId()
            );
            return;
        }

        MJ_weihai_BizLogic.getInstance().quitRoom_async(
            ctx.getFromUserId(),
            (resultX) -> buildMsgAndSend(ctx, currRoom, resultX)
        );
    }

    /**
     * 构建消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param currRoom        当前房间
     * @param resultX         业务结果
     */
    static private void buildMsgAndSend(
        MyCmdHandlerContext ctx,
        Room currRoom,
        BizResultWrapper<Boolean> resultX) {

        if (null == ctx ||
            null == currRoom ||
            null == resultX) {
            return;
        }

        if (0 != resultX.getErrorCode()) {
            ctx.sendError(
                resultX.getErrorCode(), resultX.getErrorMsg()
            );
            return;
        }

        int fromUserId = ctx.getFromUserId();

        MJ_weihai_Protocol.QuitRoomResult r0 = MJ_weihai_Protocol.QuitRoomResult.newBuilder()
            .setOk(true)
            .build();

        // 给退出房间的用户单独发送消息
        GameBroadcaster.sendMsgByUserId(fromUserId, r0);
        GameBroadcaster.removeByUserId(fromUserId);

        MJ_weihai_Protocol.QuitRoomBroadcast r1 = MJ_weihai_Protocol.QuitRoomBroadcast.newBuilder()
            .setFromUserId(fromUserId)
            .build();

        // 发送退出房间广播
        GameBroadcaster.broadcast(currRoom, r1);
    }
}
