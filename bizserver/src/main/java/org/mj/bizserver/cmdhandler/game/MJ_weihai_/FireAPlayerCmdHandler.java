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
 * 踢出一个玩家指令处理器
 */
public class FireAPlayerCmdHandler implements ICmdHandler<MJ_weihai_Protocol.FireAPlayerCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(FireAPlayerCmdHandler.class);

    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        MJ_weihai_Protocol.FireAPlayerCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        MJ_weihai_BizLogic.getInstance().fireAPlayer_async(
            fromUserId,
            cmdObj.getTargetUserId(),
            (resultX) -> buildMsgAndSend(ctx, remoteSessionId, fromUserId, cmdObj.getTargetUserId(), resultX)
        );
    }

    /**
     * 构建消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param resultX         业务结果
     */
    static private void buildMsgAndSend(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        int targetUserId,
        BizResultWrapper<Boolean> resultX) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
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

        Room currRoom = RoomGroup.getByUserId(fromUserId);

        if (null == currRoom) {
            LOGGER.error(
                "房间为空, fromUserId = {}",
                fromUserId
            );
            return;
        }

        MJ_weihai_Protocol.FireAPlayerResult
            r0 = MJ_weihai_Protocol.FireAPlayerResult.newBuilder()
            .setOk(true)
            .build();

        // 给被踢出房间的用户单独发送消息
        GameBroadcaster.sendMsgByUserId(targetUserId, r0);
        GameBroadcaster.removeByUserId(targetUserId);

        MJ_weihai_Protocol.FireAPlayerBroadcast r1 = MJ_weihai_Protocol.FireAPlayerBroadcast.newBuilder()
            .setTargetUserId(targetUserId)
            .build();

        // 发送踢出用户广播
        GameBroadcaster.broadcast(currRoom, r1);
    }
}
