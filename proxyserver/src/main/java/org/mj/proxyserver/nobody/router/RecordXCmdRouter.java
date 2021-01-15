package org.mj.proxyserver.nobody.router;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.def.ServerJobTypeEnum;
import org.mj.bizserver.foundation.MsgRecognizer;
import org.mj.comm.NettyClient;
import org.mj.proxyserver.ProxyServer;
import org.mj.proxyserver.cluster.NewServerFinder;
import org.mj.proxyserver.foundation.ClientMsgSemiFinished;
import org.mj.proxyserver.foundation.IdSetterGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 战绩相关命令处理器
 */
public class RecordXCmdRouter extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(RecordXCmdRouter.class);

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

        if (ServerJobTypeEnum.RECORD != currJobType) {
            LOGGER.error(
                "当前命令不属于战绩模块, msgCode = {}",
                msgCode
            );
            return;
        }

        // 获取路由表
        RouteTable rt = RouteTable.getOrCreate(ctx);
        // 获取已经选择的服务器 Id
        int selectServerId = rt.getServerId(ServerJobTypeEnum.RECORD);

        // 获取服务器连接
        NettyClient serverConn = ServerSelector.getServerConnByServerId(
            NewServerFinder.getInstance(),
            selectServerId
        );

        if (null == serverConn ||
            !serverConn.isReady()) {
            // 重新选择服务器
            serverConn = ServerSelector.randomAServerConnByServerJobType(
                NewServerFinder.getInstance(),
                ServerJobTypeEnum.RECORD
            );
        }

        if (null == serverConn ||
            !serverConn.isReady()) {
            LOGGER.error(
                "未找到合适的战绩服务器来接收消息, msgCode = {}",
                msgCode
            );
            return;
        }

        rt.putServerId(ServerJobTypeEnum.RECORD, serverConn.getServerId());

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
}
