package org.mj.bizserver.cmdhandler.chat;

import io.netty.channel.Channel;
import org.mj.bizserver.allmsg.ChatServerProtocol;
import org.mj.bizserver.base.AsyncOperationProcessorSingleton;
import org.mj.bizserver.base.MyCmdHandlerContext;
import org.mj.bizserver.base.ProxyServerChannelGroup;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 发送表情指令处理器
 */
public class SendEmojiCmdHandler implements ICmdHandler<MyCmdHandlerContext, ChatServerProtocol.SendEmojiCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(SendEmojiCmdHandler.class);

    @Override
    public void handle(
        MyCmdHandlerContext ctx,
        ChatServerProtocol.SendEmojiCmd cmdObj) {

        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        LOGGER.debug(
            "收到表情, fromUserId = {}, msgId = {}",
            ctx.getFromUserId(),
            cmdObj.getEmojiId()
        );

        AsyncOperationProcessorSingleton.getInstance().process(
            () -> buildResultMsgAndSend(
                ctx,
                cmdObj.getEmojiId()
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx     客户端信道处理器上下文
     * @param emojiId 表情 Id
     */
    static private void buildResultMsgAndSend(MyCmdHandlerContext ctx, int emojiId) {
        if (null == ctx) {
            return;
        }

        ChatServerProtocol.SendEmojiResult r = ChatServerProtocol.SendEmojiResult.newBuilder()
            .setEmojiId(emojiId)
            .setOk(true)
            .build();

        ctx.writeAndFlush(r);

        // 根据用户 Id 搜寻同在一个房间内的其他玩家
        List<RoomPlayerSearcher.SearchResult> otherPlayerList = RoomPlayerSearcher.searchOtherzByUserId(ctx.getFromUserId());

        if (null == otherPlayerList ||
            otherPlayerList.isEmpty()) {
            return;
        }

        ChatServerProtocol.SendEmojiBroadcast b = null;

        for (RoomPlayerSearcher.SearchResult otherPlayer : otherPlayerList) {
            if (null == otherPlayer) {
                continue;
            }

            // 获取另外一个代理服务器 Id
            final int otherProxyServerId = otherPlayer.getAtProxyServerId();
            // 获取代理服务器信道
            final Channel proxyServerCh = ProxyServerChannelGroup.getByProxyServerId(otherProxyServerId);

            if (null == proxyServerCh) {
                LOGGER.error(
                    "未找到代理服务器信道, otherProxyServerId = {}",
                    otherProxyServerId
                );
                continue;
            }

            if (null == b) {
                b = ChatServerProtocol.SendEmojiBroadcast.newBuilder()
                    .setFromUserId(ctx.getFromUserId())
                    .setEmojiId(emojiId)
                    .build();
            }

            proxyServerCh.writeAndFlush(b);
        }
    }
}
