package org.mj.bizserver.cmdhandler.passport;

import org.mj.bizserver.allmsg.PassportServerProtocol;
import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.foundation.MyCmdHandlerContext;
import org.mj.bizserver.mod.userlogin.UserLoginBizLogic;
import org.mj.bizserver.mod.userlogin.bizdata.LoginResult;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户登录命令处理器
 */
public class UserLoginCmdHandler implements ICmdHandler<MyCmdHandlerContext, PassportServerProtocol.UserLoginCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx, PassportServerProtocol.UserLoginCmd cmdObj) {
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
            (resultX) -> buildResultMsgAndSend(
                ctx, resultX
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx     客户端信道处理器上下文
     * @param resultX 业务结果
     */
    static private void buildResultMsgAndSend(
        MyCmdHandlerContext ctx, BizResultWrapper<LoginResult> resultX) {
        if (null == ctx ||
            null == resultX) {
            return;
        }

        if (0 != resultX.getErrorCode()) {
            // 写出错误消息
            ctx.sendError(
                resultX.getErrorCode(), resultX.getErrorMsg()
            );
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

        ctx.writeAndFlush(b.build());
    }
}
