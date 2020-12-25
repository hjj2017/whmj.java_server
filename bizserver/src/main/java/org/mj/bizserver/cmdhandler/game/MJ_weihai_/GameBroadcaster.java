package org.mj.bizserver.cmdhandler.game.MJ_weihai_;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.IWordz;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 游戏广播员
 */
public final class GameBroadcaster {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameBroadcaster.class);

    /**
     * 信道字典, key = userId
     */
    static private final Map<Integer, UserProfile> _innerMap = new ConcurrentHashMap<>();

    /**
     * 类默认构造器
     */
    private GameBroadcaster() {
    }

    /**
     * 添加信道
     *
     * @param ctx             信道处理器上下文
     * @param remoteSessionId 远程会话 Id
     * @param userId          用户 Id
     */
    static public void add(ChannelHandlerContext ctx, int remoteSessionId, int userId) {
        if (null != ctx) {
            add(ctx.channel(), remoteSessionId, userId);
        }
    }

    /**
     * 添加信道
     *
     * @param ch              信道
     * @param remoteSessionId 远程会话 Id
     * @param userId          用户 Id
     */
    static public void add(Channel ch, int remoteSessionId, int userId) {
        if (null == ch ||
            remoteSessionId <= 0 ||
            userId <= 0) {
            return;
        }

        final UserProfile prof = new UserProfile();
        prof._ch = ch;
        prof._remoteSessionId = remoteSessionId;
        prof._userId = userId;

        // 注意: 这里不能使用 putIfAbsent,
        // 用户重新连接代理服务器之后,
        // 代理服务器会给用户分配新的会话 Id!
        // 那么 remoteSessionId 就会有变化...
        _innerMap.put(userId, prof);
    }

    /**
     * 根据用户 Id 移除广播列表
     *
     * @param userId 用户 Id
     */
    static public void removeByUserId(int userId) {
        if (userId < 0) {
            return;
        }

        _innerMap.remove(userId);
        _innerMap.values().removeIf(UserProfile::isChannelInactive);
    }

    /**
     * 根据用户 Id 获取信道
     *
     * @param userId 用户 Id
     * @return 信道
     */
    static private Channel getChannelByUserId(int userId) {
        if (userId <= 0) {
            return null;
        }

        final UserProfile prof = _innerMap.get(userId);

        if (null != prof &&
            prof._userId == userId) {
            return prof._ch;
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
    static void sendMsgByUserId(int userId, GeneratedMessageV3 msgObj) {
        if (userId <= 0 ||
            null == msgObj) {
            return;
        }

        // 获取信道
        Channel ch = getChannelByUserId(userId);

        if (null == ch) {
            LOGGER.error(
                "信道为空, userId = {}",
                userId
            );
            return;
        }

        final InternalServerMsg newMsg = new InternalServerMsg();
        newMsg.setRemoteSessionId(getRemoteSessionIdByUserId(userId));
        newMsg.setFromUserId(userId);
        newMsg.putProtoMsg(msgObj);

        // 写出消息
        ch.writeAndFlush(newMsg);
    }

    /**
     * 根据当前房间广播消息
     *
     * @param currRoom 当前房间
     * @param msgObj   消息对象
     */
    static public void broadcast(Room currRoom, GeneratedMessageV3 msgObj) {
        if (null == currRoom ||
            null == msgObj) {
            return;
        }

        // 获取玩家列表
        List<Player> pList = currRoom.getPlayerListCopy();

        for (Player pCurr : pList) {
            if (null == pCurr) {
                continue;
            }

            // 根据用户 Id 发送消息
            sendMsgByUserId(pCurr.getUserId(), msgObj);
        }
    }

    /**
     * 根据当前牌局广播消息
     *
     * @param currRound 当前牌局
     * @param msgObj    消息对象
     */
    static public void broadcast(Round currRound, GeneratedMessageV3 msgObj) {
        if (null == currRound ||
            null == msgObj) {
            return;
        }

        // 获取玩家列表
        List<Player> pList = currRound.getPlayerListCopy();

        for (Player pCurr : pList) {
            if (null == pCurr) {
                continue;
            }

            // 根据用户 Id 发送消息
            sendMsgByUserId(pCurr.getUserId(), msgObj);
        }
    }

    /**
     * 处理记者小队
     *
     * @param rptrTeam 接着小队
     */
    static public void processReporterTeam(ReporterTeam rptrTeam) {
        if (null == rptrTeam) {
            return;
        }

        // 获取当前房间
        Room currRoom = RoomGroup.getByRoomId(rptrTeam.getRoomId());
        processReporterTeam(currRoom, rptrTeam);
    }

    /**
     * 处理记者小队
     *
     * @param currRoom 当前房间
     * @param rptrTeam 记者小队
     */
    static public void processReporterTeam(Room currRoom, ReporterTeam rptrTeam) {
        if (null == rptrTeam) {
            return;
        }

        // 获取私人词条列表
        List<IWordz> wList = rptrTeam.getPrivateWordzList();

        for (IWordz w : wList) {
            if (null == w) {
                continue;
            }

            // 根据用户 Id 发送消息
            sendMsgByUserId(
                w.getUserId(), w.buildResultMsg()
            );
        }

        if (null == currRoom) {
            return;
        }

        // 获取公共词条列表
        wList = rptrTeam.getPublicWordzList();

        for (IWordz w : wList) {
            if (null == w) {
                continue;
            }

            // 构建消息对象
            final GeneratedMessageV3 msgObj = w.buildBroadcastMsg();

            broadcast(
                currRoom, msgObj
            );
        }
    }

    /**
     * 用户资料,
     * 在这里记录了信道处理器上下文、远程会话 Id 和用户 Id
     */
    static private class UserProfile {
        /**
         * 客户端信道
         */
        Channel _ch;

        /**
         * 远程会话 Id
         */
        int _remoteSessionId;

        /**
         * 用户 Id
         */
        int _userId;

        /**
         * 客户端信道是否是不活跃的
         *
         * @return true = 是的, false = 不是
         */
        boolean isChannelInactive() {
            return null == _ch || !_ch.isActive();
        }
    }
}
