package org.mj.bizserver.cmdhandler.club;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 亲友圈广播员
 */
public final class ClubBroadcaster {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ClubBroadcaster.class);

    /**
     * 用户资料字典
     */
    static private final Map<Integer, UserProfile> _innerMap = new ConcurrentHashMap<>();

    /**
     * 类默认构造器
     */
    private ClubBroadcaster() {
    }

    /**
     * 添加听众
     *
     * @param ctx             信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param userId          用户 Id
     * @param clubId          亲友圈 Id
     */
    static public void addAudience(
        ChannelHandlerContext ctx, int remoteSessionId, int userId, int clubId) {
        if (null == ctx ||
            remoteSessionId <= 0 ||
            userId <= 0 ||
            clubId <= 0) {
            return;
        }

        final UserProfile prof = new UserProfile();
        prof._ctx = ctx;
        prof._remoteSessionId = remoteSessionId;
        prof._userId = userId;
        prof._clubId = clubId;

        // 注意: 这里不能使用 putIfAbsent,
        // 用户重新连接代理服务器之后,
        // 代理服务器会给用户分配新的会话 Id!
        // 那么 remoteSessionId 就会有变化...
        _innerMap.put(userId, prof);
    }

    /**
     * 根据用户 Id 移除听众
     *
     * @param userId 用户 Id
     */
    static public void removeAudienceByUserId(int userId) {
        _innerMap.remove(userId);
    }

    /**
     * 根据用户 Id 获取信道处理器上下文
     *
     * @param userId 用户 Id
     * @return 信道处理器上下文
     */
    static private ChannelHandlerContext getContextByUserId(int userId) {
        if (userId <= 0) {
            return null;
        }

        final UserProfile prof = _innerMap.get(userId);

        if (null != prof &&
            prof._userId == userId) {
            return prof._ctx;
        } else {
            return null;
        }
    }

    /**
     * 根据用户 Id 获取远程会话 Id
     *
     * @param userId 用户 Id
     * @return 远程会话 Id
     */
    static private int getRemoteSessionIdByUserId(int userId) {
        if (userId <= 0) {
            return -1;
        }

        final UserProfile prof = _innerMap.get(userId);

        if (null != prof &&
            prof._userId == userId) {
            return prof._remoteSessionId;
        } else {
            return -1;
        }
    }

    /**
     * 根据用户 Id 发送消息, 也就是单独发消息给某个用户
     *
     * @param userId 用户 Id
     * @param msgObj 消息对象
     */
    static private void sendMsgByUserId(int userId, GeneratedMessageV3 msgObj) {
        if (userId <= 0 ||
            null == msgObj) {
            return;
        }

        // 获取信道处理器上下文
        ChannelHandlerContext ctx = getContextByUserId(userId);

        if (null == ctx) {
            LOGGER.error(
                "信道处理器上下文为空, userId = {}",
                userId
            );
            return;
        }

        final InternalServerMsg newMsg = new InternalServerMsg();
        newMsg.setRemoteSessionId(getRemoteSessionIdByUserId(userId));
        newMsg.setFromUserId(userId);
        newMsg.putProtoMsg(msgObj);

        // 写出消息
        ctx.channel().writeAndFlush(newMsg);
    }

    /**
     * 根据当前房间广播消息
     *
     * @param clubId 亲友圈 Id
     * @param msgObj 消息对象
     */
    static private void broadcast(int clubId, GeneratedMessageV3 msgObj) {
        if (clubId <= 0 ||
            null == msgObj) {
            return;
        }

        for (UserProfile ctx : _innerMap.values()) {
            if (null == ctx ||
                ctx._clubId != clubId) {
                continue;
            }

            // 根据用户 Id 发送消息
            sendMsgByUserId(ctx._userId, msgObj);
        }
    }

    /**
     * 用户资料
     */
    static private class UserProfile {
        /**
         * 信道处理器上下文
         */
        private ChannelHandlerContext _ctx;

        /**
         * 远程会话 Id
         */
        private int _remoteSessionId;

        /**
         * 用户 Id
         */
        private int _userId;

        /**
         * 亲友圈 Id
         */
        private int _clubId;
    }
}
