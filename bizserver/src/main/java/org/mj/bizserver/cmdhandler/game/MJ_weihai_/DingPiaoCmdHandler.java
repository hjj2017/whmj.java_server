package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.base.BizResultWrapper;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.MJ_weihai_BizLogic;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 定飘指令处理器
 */
public class DingPiaoCmdHandler implements ICmdHandler<MyCmdHandlerContext, MJ_weihai_Protocol.DingPiaoCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        MJ_weihai_Protocol.DingPiaoCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // 业务结果
        final BizResultWrapper<ReporterTeam> resultX = new BizResultWrapper<>();

        // 定飘
        MJ_weihai_BizLogic.getInstance().dingPiao(
            ctx.getFromUserId(),
            cmdObj.getPiaoX(),
            resultX
        );

        // 收集回放词条列表
        collectPlaybackWordzList(resultX);
        // 构建消息并发送
        buildMsgAndSend(ctx, resultX);
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
     * @param ctx     客户端信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildMsgAndSend(
        MyCmdHandlerContext ctx,
        BizResultWrapper<ReporterTeam> resultX) {

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

        GameBroadcaster.processReporterTeam(resultX.getFinalResult());
    }
}
