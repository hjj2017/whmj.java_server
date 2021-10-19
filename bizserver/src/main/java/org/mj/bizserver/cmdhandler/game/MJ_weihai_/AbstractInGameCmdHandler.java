package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.game.MJ_weihai_.RoomOverDetermine;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象的游戏中的命令处理器
 *
 * @param <TCmd> 命令参数泛型
 */
abstract public class AbstractInGameCmdHandler<TCmd extends GeneratedMessageV3> implements ICmdHandler<MyCmdHandlerContext, TCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(AbstractInGameCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx, TCmd cmdObj) {
        // 将复杂工作交给代理, 我只执行省心的调用
        doProxyInvoke(ctx, cmdObj);
    }

    /**
     * 执行代理调用
     *
     * @param ctx    信道处理器上下文
     * @param cmdObj 命令对象
     */
    protected void doProxyInvoke(
        MyCmdHandlerContext ctx, TCmd cmdObj) {
        //
        // XXX 注意: 如果胡牌成功, 当前牌局会标注为已结束!
        // 再调用 RoomGroup#getByRoomId 函数和 Room#getCurrRound 函数,
        // 拿回来的牌局对象就等于空值...
        // 所以我们事先取出当前牌局, 做出基本判断之后再往下走,
        // 因为胡牌之后还要发送牌局结算结果,
        // 做出基本判断之后也避免了重复发送结果的问题
        //
        // 来自用户 Id
        int fromUserId = ctx.getFromUserId();

        // 获取当前房间
        final Room currRoom = RoomGroup.getByUserId(fromUserId);

        if (null == currRoom ||
            RoomOverDetermine.determine(currRoom) ||
            currRoom.isForcedEnd()) {
            LOGGER.error(
                "当前房间为空或者已经结束! fromUserId = {}",
                fromUserId
            );
            return;
        }

        if (null != currRoom.getDissolveRoomSession()) {
            LOGGER.error(
                "当前房间正准备解散, userId = {}, atRoomId = {}",
                fromUserId,
                currRoom.getRoomId()
            );
            return;
        }

        // 获取当前牌局
        final Round currRound = currRoom.getCurrRound();

        if (null == currRound ||
            currRound.isEnded()) {
            LOGGER.error(
                "当前牌局为空或者已经结束! fromUserId = {}, atRoomId = {}",
                fromUserId,
                currRoom.getRoomId()
            );
            return;
        }

        // 业务结果
        final BizResultWrapper<ReporterTeam> resultX = new BizResultWrapper<>();
        // 处理命令对象并更新业务结果
        doEasyInvoke(fromUserId, cmdObj, resultX);

        if (null != resultX.getFinalResult()) {
            currRound.addPlaybackWordzList(
                resultX.getFinalResult().getPlaybackWordzList()
            );
        }

        // 构建消息并发送
        buildMsgAndSend(
            ctx, resultX, currRoom
        );

        // 当牌局结束
        onRoundEnded(currRound);
        // 当房间结束
        onRoomEnded(currRoom);
    }

    /**
     * 执行省心的调用
     *
     * @param fromUserId 来自用户 Id
     * @param cmdObj     命令对象
     * @param resultX    业务结果
     */
    abstract protected void doEasyInvoke(int fromUserId, TCmd cmdObj, BizResultWrapper<ReporterTeam> resultX);

    /**
     * 构建消息并发送
     * XXX 注意: 胡牌之后, 当前房间已经不能通过 RoomGroup.getByRoomId 方式来获取了
     *
     * @param ctx     客户端信道处理器上下文
     * @param resultX 业务结果
     * @param atRoom  所在房间
     */
    static private void buildMsgAndSend(
        MyCmdHandlerContext ctx,
        BizResultWrapper<ReporterTeam> resultX,
        Room atRoom) {

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

        GameBroadcaster.processReporterTeam(
            atRoom,
            resultX.getFinalResult()
        );
    }

    /**
     * 当牌局结束
     *
     * @param currRound 当前牌局
     */
    static private void onRoundEnded(Round currRound) {
        if (null == currRound ||
            !currRound.isEnded()) {
            return;
        }

        LOGGER.info(
            "当前牌局结束, 准备发送牌局结算结果. atRoomId = {}, roundIndex = {}",
            currRound.getRoomId(),
            currRound.getRoundIndex()
        );

        // 投递牌局结算结果
        RoundSettlementPostman.post(currRound);
    }

    /**
     * 当前房间结束
     *
     * @param currRoom 当前房间
     */
    static private void onRoomEnded(Room currRoom) {
        if (null == currRoom ||
            !RoomOverDetermine.determine(currRoom)) {
            return;
        }

        LOGGER.info(
            "所有牌局都已结束, 准备发送房间结算结果. atRoomId = {}",
            currRoom.getRoomId()
        );

        RoomSettlementPostman.post(currRoom);
    }
}
