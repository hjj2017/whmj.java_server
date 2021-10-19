package org.mj.bizserver.cmdhandler.hall;

import org.mj.bizserver.allmsg.HallServerProtocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.userinfo.UserInfoBizLogic;
import org.mj.bizserver.mod.userinfo.bizdata.UserDetailz;
import org.mj.comm.cmdhandler.ICmdHandler;

/**
 * 获取我的详情
 */
public class GetMyDetailzCmdHandler implements ICmdHandler<MyCmdHandlerContext, HallServerProtocol.GetMyDetailzCmd> {
    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        HallServerProtocol.GetMyDetailzCmd cmdObj) {

        UserInfoBizLogic.getInstance().getUserDetailzByUserId_async(
            ctx.getFromUserId(),
            (resultX) -> buildResultMsgAndSend(ctx, resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx     客户端信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, BizResultWrapper<UserDetailz> resultX) {
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

        ctx.writeAndFlush(r);
    }
}
