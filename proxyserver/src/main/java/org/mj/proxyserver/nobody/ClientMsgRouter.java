package org.mj.proxyserver.nobody;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.def.ServerJobTypeEnum;
import org.mj.bizserver.base.MsgRecognizer;
import org.mj.proxyserver.base.ClientMsgSemiFinished;
import org.mj.proxyserver.nobody.router.ChatXCmdRouter;
import org.mj.proxyserver.nobody.router.ClubXCmdRouter;
import org.mj.proxyserver.nobody.router.GameXCmdRouter;
import org.mj.proxyserver.nobody.router.HallXCmdRouter;
import org.mj.proxyserver.nobody.router.PassportXCmdRouter;
import org.mj.proxyserver.nobody.router.RecordXCmdRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端消息路由器
 */
public class ClientMsgRouter extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ClientMsgRouter.class);

    /**
     * 服务器工作类型和信道处理器字典
     */
    private final Map<ServerJobTypeEnum, ChannelInboundHandler> _hMap = new ConcurrentHashMap<>();

    /**
     * 类默认构造器
     */
    public ClientMsgRouter() {
        _hMap.putIfAbsent(ServerJobTypeEnum.PASSPORT, new PassportXCmdRouter());
        _hMap.putIfAbsent(ServerJobTypeEnum.HALL, new HallXCmdRouter());
        _hMap.putIfAbsent(ServerJobTypeEnum.GAME, new GameXCmdRouter());
        _hMap.putIfAbsent(ServerJobTypeEnum.CLUB, new ClubXCmdRouter());
        _hMap.putIfAbsent(ServerJobTypeEnum.CHAT, new ChatXCmdRouter());
        _hMap.putIfAbsent(ServerJobTypeEnum.RECORD, new RecordXCmdRouter());
        // 在此添加新的服务器工作类型和处理器
    }

    /**
     * 获取游戏指令处理器
     *
     * @return 游戏指令处理器
     */
    public ChannelInboundHandler getGameXCmdRouter() {
        return _hMap.getOrDefault(ServerJobTypeEnum.GAME, null);
    }

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

        if (null == currJobType) {
            LOGGER.error(
                "未识别出服务器工作类型, msgCode = {}",
                clientMsg.getMsgCode()
            );
            return;
        }

        // 获取处理器
        ChannelInboundHandler h = _hMap.get(currJobType);

        if (null == h) {
            LOGGER.error(
                "未找到可以处理 {} 类型消息的处理器, 请修改 {} 类默认构造器增加相关代码",
                currJobType,
                ClientMsgRouter.class.getName()
            );
            return;
        }

        try {
            h.channelRead(ctx, msgObj);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
