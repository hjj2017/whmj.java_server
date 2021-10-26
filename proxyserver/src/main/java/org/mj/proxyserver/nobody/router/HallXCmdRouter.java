package org.mj.proxyserver.nobody.router;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.allmsg.HallServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.def.ServerJobTypeEnum;
import org.mj.bizserver.base.MsgRecognizer;
import org.mj.comm.util.OutParam;
import org.mj.proxyserver.ProxyServer;
import org.mj.proxyserver.cluster.NewServerFinder;
import org.mj.proxyserver.foundation.ClientMsgSemiFinished;
import org.mj.proxyserver.foundation.IdSetterGetter;
import org.mj.proxyserver.nobody.ClientMsgRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 大厅相关命令处理器
 */
public class HallXCmdRouter extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(HallXCmdRouter.class);

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

        if (HallServerProtocol.HallServerMsgCodeDef._CreateRoomCmd_VALUE == msgCode ||
            HallServerProtocol.HallServerMsgCodeDef._JoinRoomCmd_VALUE == msgCode) {
            // 修改为游戏服指令并处理
            changeToGameXCmdAndHandle(ctx, clientMsg);
            return;
        }

        // 获取当前服务器工作类型
        ServerJobTypeEnum currJobType = MsgRecognizer.getServerJobTypeByMsgCode(msgCode);

        if (ServerJobTypeEnum.HALL != currJobType) {
            LOGGER.error(
                "当前命令不属于大厅模块, msgCode = {}",
                msgCode
            );
            return;
        }

        // 获取路由表
        RouteTable rt = RouteTable.getOrCreate(ctx);
        // 获取已经选择的服务器 Id
        int selectServerId = rt.getServerId(ServerJobTypeEnum.HALL);
        // 最新版本号
        OutParam<Long> out_newestRev = new OutParam<>();

        // 获取服务器连接
        NettyClient serverConn = ServerSelector.getServerConnByServerId(
            NewServerFinder.getInstance(),
            selectServerId,
            out_newestRev // 取出最新版本号
        );

        if (null == serverConn ||
            !serverConn.isReady() ||
            rt.getRev(currJobType) != OutParam.optVal(out_newestRev, -1L)) {
            // 重新选择服务器
            serverConn = ServerSelector.randomAServerConnByServerJobType(
                NewServerFinder.getInstance(),
                ServerJobTypeEnum.HALL,
                out_newestRev // 取出最新版本号
            );
        }

        if (null == serverConn ||
            !serverConn.isReady()) {
            LOGGER.error(
                "未找到合适的大厅服务器来接收消息, msgCode = {}",
                msgCode
            );
            return;
        }

        rt.putServerIdAndRev(
            ServerJobTypeEnum.HALL, serverConn.getServerId(),
            OutParam.optVal(out_newestRev, -1L)
        );

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
     * 修改为游戏服指令并处理
     *
     * @param ctx       信道处理器上下文
     * @param clientMsg 客户端消息
     */
    static private void changeToGameXCmdAndHandle(ChannelHandlerContext ctx, ClientMsgSemiFinished clientMsg) {
        if (null == ctx ||
            null == clientMsg) {
            return;
        }

        try {
            // 如果是创建和加入房间消息,
            // 则转发到游戏服务器执行...
            ctx.pipeline().get(ClientMsgRouter.class)
                .getGameXCmdRouter()
                .channelRead(ctx, clientMsg);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
