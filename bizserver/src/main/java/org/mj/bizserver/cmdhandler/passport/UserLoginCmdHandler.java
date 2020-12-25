package org.mj.bizserver.cmdhandler.passport;

import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.allmsg.PassportServerProtocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.userlogin.UserLoginBizLogic;
import org.mj.bizserver.mod.userlogin.bizdata.LoginResult;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户登录命令处理器
 */
public class UserLoginCmdHandler implements ICmdHandler<PassportServerProtocol.UserLoginCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, PassportServerProtocol.UserLoginCmd cmdObj) {
        if (null == cmdObj) {
            return;
        }

        LOGGER.debug(
            "收到用户登录消息, propertyStr = {}",
            cmdObj.getPropertyStr()
        );

        // 执行用户登陆逻辑
        UserLoginBizLogic.getInstance().doUserLogin_async(
            cmdObj.getLoginMethod(),
            cmdObj.getPropertyStr(),
            (resultX) -> buildResultMsgAndSend(ctx, remoteSessionId, fromUserId, resultX)
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param resultX         业务结果
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, BizResultWrapper<LoginResult> resultX) {
        if (null == ctx ||
            null == resultX) {
            return;
        }

        final InternalServerMsg newMsg = new InternalServerMsg();
        newMsg.setRemoteSessionId(remoteSessionId);
        newMsg.setFromUserId(fromUserId);

        if (0 != newMsg.admitError(resultX)) {
            ctx.writeAndFlush(newMsg);
            return;
        }

        // 获取最终结果
        final LoginResult loginResult = resultX.getFinalResult();

        // 登录成功
        PassportServerProtocol.UserLoginResult.Builder b = PassportServerProtocol.UserLoginResult.newBuilder();
        b.setUserId(loginResult.getUserId());
        b.setUserName(loginResult.getUserName());
        b.setTicket(loginResult.getTicket());
        b.setUkeyStr(loginResult.getUkeyStr());
        b.setUkeyExpireAt(loginResult.getUkeyExpireAt());

        PassportServerProtocol.UserLoginResult r = b.build();

        newMsg.putProtoMsg(r);
        ctx.writeAndFlush(newMsg);
    }
}
