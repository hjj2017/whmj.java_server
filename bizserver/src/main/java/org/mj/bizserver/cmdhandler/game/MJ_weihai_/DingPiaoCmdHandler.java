package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 定飘指令处理器
 */
public class DingPiaoCmdHandler implements ICmdHandler<MJ_weihai_Protocol.DingPiaoCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        MJ_weihai_Protocol.DingPiaoCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        // 业务结果
        final BizResultWrapper<ReporterTeam> resultX = new BizResultWrapper<>();

        // 定飘
        MJ_weihai_BizLogic.getInstance().dingPiao(
            fromUserId,
            cmdObj.getPiaoX(),
            resultX
        );

        // 收集回放词条列表
        collectPlaybackWordzList(resultX);
        // 构建消息并发送
        buildMsgAndSend(ctx, remoteSessionId, fromUserId, resultX);
    }

    /**
     * 收集回放词条列表
     *
     * @param resultX 业务结果
     */
    static private void collectPlaybackWordzList(final BizResultWrapper<ReporterTeam> resultX) {
        if (null == resultX ||
            null == resultX.getFinalResult()) {
            return;
        }

        // 获取记者小队
        final ReporterTeam rptrTeam = resultX.getFinalResult();
        // 获取当前房间
        final Room currRoom = RoomGroup.getByRoomId(rptrTeam.getRoomId());

        if (null == currRoom) {
            return;
        }

        // 获取当前牌局
        final Round currRound = currRoom.getCurrRound();

        if (null == currRound) {
            return;
        }

        currRound.addPlaybackWordzList(
            rptrTeam.getPlaybackWordzList()
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
        BizResultWrapper<ReporterTeam> resultX) {

        if (null == ctx ||
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

        GameBroadcaster.processReporterTeam(resultX.getFinalResult());
    }
}
