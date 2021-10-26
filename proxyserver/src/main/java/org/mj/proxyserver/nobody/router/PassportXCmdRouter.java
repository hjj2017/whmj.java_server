package org.mj.proxyserver.nobody.router;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.allmsg.PassportServerProtocol;
import org.mj.bizserver.def.ServerJobTypeEnum;
import org.mj.bizserver.base.MsgRecognizer;
import org.mj.proxyserver.ProxyServer;
import org.mj.proxyserver.cluster.NewServerFinder;
import org.mj.proxyserver.foundation.ClientMsgHandler;
import org.mj.proxyserver.foundation.ClientMsgSemiFinished;
import org.mj.proxyserver.foundation.IdSetterGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 账户相关命令处理器
 */
public class PassportXCmdRouter extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(PassportXCmdRouter.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObj) {
        if (!(msgObj instanceof ClientMsgSemiFinished)) {
            if (null != ctx) {
                ctx.fireChannelRead(msgObj);
            }

            return;
        }

        ClientMsgSemiFinished clientMsg = (ClientMsgSemiFinished) msgObj;
        final int msgCode = clientMsg.getMsgCode();

        // 获取当前服务器工作类型
        ServerJobTypeEnum currJobType = MsgRecognizer.getServerJobTypeByMsgCode(msgCode);

        if (ServerJobTypeEnum.PASSPORT != currJobType) {
            LOGGER.error(
                "当前命令不属于账户模块, msgCode = {}",
                msgCode
            );
            return;
        }

        // 获取服务器连接, 也就是本机到目标服务器的连接
        // 登录过程都是随机选择一个服务器
        NettyClient serverConn = ServerSelector.randomAServerConnByServerJobType(
            NewServerFinder.getInstance(), currJobType
        );

        if (null == serverConn ||
            !serverConn.isReady()) {
            LOGGER.error(
                "未找到合适的登陆服务器来接收消息, msgCode = {}",
                msgCode
            );
            return;
        }

        if (clientMsg.getMsgCode() == PassportServerProtocol.PassportServerMsgCodeDef._UserLoginCmd_VALUE) {
            // 如果是用户登陆命令,
            // 执行补充逻辑
            supplyUserLoginCmd(ctx, clientMsg);
        }

        final InternalServerMsg innerMsg = new InternalServerMsg();
        innerMsg.setProxyServerId(ProxyServer.getId());
        innerMsg.setRemoteSessionId(IdSetterGetter.getSessionId(ctx));
        innerMsg.setFromUserId(IdSetterGetter.getUserId(ctx));
        innerMsg.setMsgCode(msgCode);
        innerMsg.setMsgBody(clientMsg.getMsgBody());

        LOGGER.info(
            "转发消息到内部服务器, msgCode = {}, targetServer = {}",
            msgCode,
            serverConn.getServerName()
        );

        serverConn.sendMsg(innerMsg);

        // 释放资源
        clientMsg.free();
    }

    /**
     * 补充用户登陆消息, 例如: clientIpAddr
     *
     * @param ctx       信道处理器上下文
     * @param clientMsg 客户端消息
     */
    static private void supplyUserLoginCmd(ChannelHandlerContext ctx, ClientMsgSemiFinished clientMsg) {
        if (null == ctx ||
            null == clientMsg) {
            return;
        }

        try {
            // 获取客户端 IP 地址
            String clientIpAddr = ClientMsgHandler.getXRealIp(ctx);

            if (null == clientIpAddr) {
                InetSocketAddress socketAddr = (InetSocketAddress) ctx.channel().remoteAddress();
                clientIpAddr = socketAddr.getAddress().getHostAddress();
            }

            // 解析为用户登陆命令
            PassportServerProtocol.UserLoginCmd cmdObj = PassportServerProtocol.UserLoginCmd.parseFrom(clientMsg.getMsgBody());

            JSONObject jsonObj = JSONObject.parseObject(cmdObj.getPropertyStr());
            jsonObj.put("clientIpAddr", clientIpAddr);

            // 创建构建器重新设置登陆方式和属性字符串
            PassportServerProtocol.UserLoginCmd.Builder b = cmdObj.newBuilderForType();
            b.setLoginMethod(cmdObj.getLoginMethod());
            b.setPropertyStr(jsonObj.toJSONString());

            // 修改消息体字节数组
            clientMsg.setMsgBody(b.build().toByteArray());
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
