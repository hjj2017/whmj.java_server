package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 退出房间指令处理器
 */
public class QuitRoomCmdHandler implements ICmdHandler<MJ_weihai_Protocol.QuitRoomCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(QuitRoomCmdHandler.class);

    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        MJ_weihai_Protocol.QuitRoomCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        Room currRoom = RoomGroup.getByUserId(fromUserId);

        if (null == currRoom) {
            LOGGER.error(
                "房间为空, fromUserId = {}",
                fromUserId
            );
            return;
        }

        MJ_weihai_BizLogic.getInstance().quitRoom_async(
            fromUserId,
            (resultX) -> buildMsgAndSend(ctx, remoteSessionId, fromUserId, currRoom, resultX)
        );
    }

    /**
     * 构建消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param currRoom        当前房间
     * @param resultX         业务结果
     */
    static private void buildMsgAndSend(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        Room currRoom,
        BizResultWrapper<Boolean> resultX) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == currRoom ||
            null == resultX) {
            return;
        }

        if (0 != resultX.getErrorCode()) {
            InternalServerMsg newMsg = new InternalServerMsg();
            newMsg.setRemoteSessionId(remoteSessionId);
            newMsg.setFromUserId(fromUserId);
            newMsg.admitError(resultX);

            ctx.writeAndFlush(newMsg);
            return;
        }

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
