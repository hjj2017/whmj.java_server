package org.mj.bizserver.cmdhandler.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.ChatServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.foundation.AsyncOperationProcessorSingleton;
import org.mj.bizserver.foundation.IdSetterGetter;
import org.mj.bizserver.foundation.ProxyServerChannelGroup;
import org.mj.comm.cmdhandler.ICmdHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 发送表情指令处理器
 */
public class SendEmojiCmdHandler implements ICmdHandler<ChatServerProtocol.SendEmojiCmd> {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(SendEmojiCmdHandler.class);

    @Override
    public void handle(
        ChannelHandlerContext ctx,
        int remoteSessionId,
        int fromUserId,
        ChatServerProtocol.SendEmojiCmd cmdObj) {

        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0 ||
            null == cmdObj) {
            return;
        }

        LOGGER.debug(
            "收到表情, fromUserId = {}, msgId = {}",
            fromUserId,
            cmdObj.getEmojiId()
        );

        AsyncOperationProcessorSingleton.getInstance().process(
            () -> buildResultMsgAndSend(
                ctx, remoteSessionId, fromUserId,
                cmdObj.getEmojiId()
            )
        );
    }

    /**
     * 构建结果消息并发送
     *
     * @param ctx             客户端信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param fromUserId      来自用户 Id
     * @param emojiId         表情 Id
     */
    static private void buildResultMsgAndSend(
        ChannelHandlerContext ctx, int remoteSessionId, int fromUserId, int emojiId) {
        if (null == ctx ||
            remoteSessionId <= 0 ||
            fromUserId <= 0) {
            return;
        }

        ChatServerProtocol.SendEmojiResult r = ChatServerProtocol.SendEmojiResult.newBuilder()
            .setEmojiId(emojiId)
            .setOk(true)
            .build();

        InternalServerMsg msg0 = new InternalServerMsg();
        msg0.setProxyServerId(IdSetterGetter.getProxyServerId(ctx));
        msg0.setRemoteSessionId(remoteSessionId);
        msg0.setFromUserId(fromUserId);
        msg0.putProtoMsg(r);

        ctx.writeAndFlush(msg0);

        // 根据用户 Id 搜寻同在一个房间内的其他玩家
        List<RoomPlayerSearcher.SearchResult> otherPlayerList = RoomPlayerSearcher.searchOtherzByUserId(fromUserId);

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
                    .setFromUserId(fromUserId)
                    .setEmojiId(emojiId)
                    .build();
            }

            InternalServerMsg msg1 = new InternalServerMsg();
            msg1.setProxyServerId(IdSetterGetter.getProxyServerId(ctx));
            msg1.setRemoteSessionId(otherPlayer.getRemoteSessionId());
            msg1.setFromUserId(otherPlayer.getUserId());
            msg1.putProtoMsg(b);

            proxyServerCh.writeAndFlush(msg1);
        }
    }
}
