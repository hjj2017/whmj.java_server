package org.mj.proxyserver.base;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 客户端信道分组
 */
public final class ClientChannelGroup {
    /**
     * 用户 Id 和会话 Id 字典
     */
    static private final Map<Integer, Integer> _userIdAndSessionIdMap = new ConcurrentHashMap<>();

    /**
     * 客户端信道字典
     */
    static private final Map<Integer, Channel> _chMap = new ConcurrentHashMap<>();

    /**
     * 私有化类默认构造器
     */
    private ClientChannelGroup() {
    }

    /**
     * 根据上下文添加客户端信道
     *
     * @param ctx 客户端信道上下文
     */
    static public void add(ChannelHandlerContext ctx) {
        if (null != ctx) {
            add(ctx.channel());
        }
    }

    /**
     * 添加客户端信道
     *
     * @param ch 客户端信道
     */
    static public void add(Channel ch) {
        if (null == ch) {
            return;
        }

        int sessionId = IdSetterGetter.getSessionId(ch);
        _chMap.put(sessionId, ch);
    }

    /**
     * 根据上下文移除客户端信道
     *
     * @param ctx 客户端信道上下文
     */
    static public void remove(ChannelHandlerContext ctx) {
        if (null != ctx) {
            remove(ctx.channel());
        }
    }

    /**
     * 移除客户端信道
     *
     * @param ch 客户端信道
     * @see org.mj.proxyserver.nobody.CheckInTicketCmdHandler
     * @see ClientMsgHandler
     */
    static public void remove(Channel ch) {
        if (null == ch) {
            return;
        }

        int userId = IdSetterGetter.getUserId(ch);
        int sessionId = IdSetterGetter.getSessionId(ch);

        if (sessionId == _userIdAndSessionIdMap.getOrDefault(userId, -1)) {
            // 只有用户 Id 和 sessionId 都相同时,
            // 才从字典里移除...
            // 用户在重新连接服务器时,
            // 先处理的是 CheckInTicketCmdHandler.handle_OFFICIAL,
            // 之后才执行 ClientMsgHandler.channelInactive,
            // 这样的调用顺序会导致刚添加的 userId 会被马上移除!
            // 账号互踢就失败了...
            _userIdAndSessionIdMap.remove(userId);
        }

        _chMap.values().remove(ch);
        _chMap.values().removeIf(
            (diedChannel) -> !diedChannel.isActive()
        );
    }

    /**
     * 根据用户 Id 移除
     *
     * @param userId 用户 Id
     */
    static public Channel removeByUserId(int userId) {
        Integer sessionId = _userIdAndSessionIdMap.remove(userId);

        if (null != sessionId) {
            return _chMap.remove(sessionId);
        } else {
            return null;
        }
    }

    /**
     * 根据会话 Id 移除客户端信道
     *
     * @param sessionId 会话 Id
     * @return 客户端信道
     */
    static public Channel removeBySessionId(int sessionId) {
        if (sessionId <= 0) {
            return null;
        }

        _userIdAndSessionIdMap.values().remove(sessionId);
        return _chMap.remove(sessionId);
    }

    /**
     * 根据用户 Id 获取客户端信道
     *
     * @param userId 用户 Id
     * @return 客户端信道
     */
    static public Channel getByUserId(int userId) {
        // 获取会话 Id
        Integer sessionId = _userIdAndSessionIdMap.get(userId);

        if (null == sessionId) {
            return null;
        }

        Channel ch = _chMap.get(sessionId);

        if (null == ch) {
            _userIdAndSessionIdMap.remove(userId);
        }

        return ch;
    }

    /**
     * 根据会话 Id 获取客户端信道
     *
     * @param sessionId 会话 Id
     * @return 客户端信道
     */
    static public Channel getBySessionId(int sessionId) {
        if (sessionId <= 0) {
            return null;
        }

        Channel ch = _chMap.get(sessionId);

        if (null == ch) {
            _userIdAndSessionIdMap.values().remove(sessionId);
        }

        return ch;
    }

    /**
     * 根据会话 Id 写出消息
     *
     * @param msgObj    消息对象
     * @param sessionId 会话 Id
     */
    static public void writeAndFlushBySessionId(Object msgObj, int sessionId) {
        if (null == msgObj) {
            return;
        }

        // 获取客户端信道
        Channel ch = getBySessionId(sessionId);

        if (null != ch) {
            ch.writeAndFlush(msgObj);
        }
    }

    /**
     * 关联用户 Id 与会话 Id
     *
     * @param userId    用户 Id
     * @param sessionId 会话 Id
     */
    static public void relative(int userId, int sessionId) {
        _userIdAndSessionIdMap.put(userId, sessionId);
    }

    /**
     * 便利所有的客户端信道
     *
     * @param action 处理行为
     */
    static public void forEachChannel(Consumer<Channel> action) {
        if (null != action) {
            _chMap.values().iterator()
                .forEachRemaining(action);
        }
    }
}
