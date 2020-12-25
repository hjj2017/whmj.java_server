package org.mj.bizserver.cmdhandler.hall;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.HallServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 获取我的详情
 */
public class GetMyDetailzCmdHandler implements ICmdHandler<HallServerProtocol.GetMyDetailzCmd> {
    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        HallServerProtocol.GetMyDetailzCmd cmdObj) {

        UserInfoBizLogic.getInstance().getUserDetailzByUserId_async(
            fromUserId,
            (resultX) -> buildResultMsgAndSend(ctx, remoteSessionId, fromUserId, resultX)
        );
    }

    /**
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, BizResultWrapper<UserDetailz> resultX) {
        if (null == ctx ||
            null == resultX) {
            return;
        }

        InternalServerMsg newMsg = new InternalServerMsg();
        newMsg.setRemoteSessionId(remoteSessionId);
        newMsg.setFromUserId(fromUserId);

        if (0 != newMsg.admitError(resultX)) {
            ctx.writeAndFlush(newMsg);
            return;
        }

        // 获取用户详情
        final UserDetailz userDetailz = resultX.getFinalResult();

        HallServerProtocol.GetMyDetailzResult.Builder b = HallServerProtocol.GetMyDetailzResult.newBuilder();

        if (null != userDetailz) {
            b.setUserId(userDetailz.getUserId());
            b.setUserName(userDetailz.getUserName());
            b.setHeadImg(userDetailz.getHeadImg());
            b.setSex(userDetailz.getSex());
            b.setLastLoginIp(userDetailz.getLastLoginIp());
            b.setRoomCard(userDetailz.getRoomCard());
        }

        HallServerProtocol.GetMyDetailzResult r = b.build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
