package org.mj.proxyserver.nobody;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.allmsg.HallServerProtocol;
import org.mj.bizserver.allmsg.InternalServerMsg;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.bizserver.def.ServerJobTypeEnum;
import org.mj.bizserver.foundation.MsgRecognizer;
import org.mj.comm.NettyClient;
import org.mj.comm.util.RedisXuite;
import org.mj.proxyserver.ProxyServer;
import org.mj.proxyserver.cluster.NewServerFinder;
import org.mj.proxyserver.foundation.ClientMsgSemiFinished;
import org.mj.proxyserver.foundation.IdSetterGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.net.InetSocketAddress;

/**
 * 游戏相关命令处理器
 */
public class GameXCmdHandler extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(GameXCmdHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObj) {
        if (!(msgObj instanceof ClientMsgSemiFinished)) {
            if (null != ctx) {
                ctx.fireChannelRead(msgObj);
            }

            return;
        }

        // 获取用户 Id
        final int userId = IdSetterGetter.getUserId(ctx);

        if (userId <= 0) {
            LOGGER.error("未找到用户 Id");
            return;
        }

        ClientMsgSemiFinished clientMsg = (ClientMsgSemiFinished) msgObj;
        final int msgCode = clientMsg.getMsgCode();

        // 获取当前服务器工作类型
        ServerJobTypeEnum currJobType = MsgRecognizer.getServerJobTypeByMsgCode(msgCode);

        if (HallServerProtocol.HallServerMsgCodeDef._CreateRoomCmd_VALUE != msgCode &&
            HallServerProtocol.HallServerMsgCodeDef._JoinRoomCmd_VALUE != msgCode &&
            ClubServerProtocol.ClubServerMsgCodeDef._CreateTableCmd_VALUE != msgCode &&
            ClubServerProtocol.ClubServerMsgCodeDef._JoinTableCmd_VALUE != msgCode &&
            ServerJobTypeEnum.GAME != currJobType) {
            // XXX 注意: 游戏模块可以处理大厅模块的创建和加入房间消息
            LOGGER.error(
                "当前命令不属于游戏模块, msgCode = {}",
                msgCode
            );
            return;
        }

        if (clientMsg.getMsgCode() == MJ_weihai_Protocol.MJ_weihai_MsgCodeDef._ReportGeoLocationCmd_VALUE) {
            // 如果是上报地理位置指令,
            // 执行补充逻辑
            supplyReportGeoLocationCmd(ctx, clientMsg);
        }

        // 服务器连接
        NettyClient serverConn;

        if (HallServerProtocol.HallServerMsgCodeDef._JoinRoomCmd_VALUE == msgCode ||
            ClubServerProtocol.ClubServerMsgCodeDef._JoinTableCmd_VALUE == msgCode) {
            // 如果是加入房间或 ( 亲友圈 ) 牌桌,
            // 那么根据房间 Id 获取服务器连接
            int roomId = getRoomId(clientMsg);
            serverConn = getGameServerConnByRoomId(roomId);
        } else {
            // 如果是其他游戏相关消息,
            // 则通过用户 Id 获取服务器连接
            serverConn = getGameServerConnByUserId(userId);
        }

        if (null == serverConn ||
            !serverConn.isReady()) {
            LOGGER.error(
                "未找到合适的游戏服务器来接收消息, msgCode = {}",
                msgCode
            );
            return;
        }

        final InternalServerMsg innerMsg = new InternalServerMsg();
        innerMsg.setProxyServerId(ProxyServer.getId());
        innerMsg.setRemoteSessionId(IdSetterGetter.getSessionId(ctx));
        innerMsg.setFromUserId(userId);
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
     * 补充上报地理位置指令
     *
     * @param ctx       信道处理器上下文
     * @param clientMsg 客户端消息
     */
    static private void supplyReportGeoLocationCmd(ChannelHandlerContext ctx, ClientMsgSemiFinished clientMsg) {
        if (null == ctx ||
            null == clientMsg) {
            return;
        }

        try {
            InetSocketAddress socketAddr = (InetSocketAddress) ctx.channel().remoteAddress();
            final String clientIpAddr = socketAddr.getAddress().getHostAddress();

            // 解析为用户登陆命令
            MJ_weihai_Protocol.ReportGeoLocationCmd cmdObj = MJ_weihai_Protocol.ReportGeoLocationCmd.parseFrom(clientMsg.getMsgBody());

            // 创建构建器重新设置登陆方式和属性字符串
            MJ_weihai_Protocol.ReportGeoLocationCmd.Builder b = cmdObj.newBuilderForType();
            b.setClientIpAddr(clientIpAddr);
            b.setLatitude(cmdObj.getLatitude());
            b.setLongitude(cmdObj.getLongitude());
            b.setAltitude(cmdObj.getAltitude());

            // 修改消息体字节数组
            clientMsg.setMsgBody(b.build().toByteArray());
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取房间 Id
     *
     * @param clientMsg 客户端消息半成品
     * @return 房间 Id
     */
    private int getRoomId(ClientMsgSemiFinished clientMsg) {
        if (null == clientMsg) {
            return -1;
        }

        try {
            if (HallServerProtocol.HallServerMsgCodeDef._JoinRoomCmd_VALUE == clientMsg.getMsgCode()) {
                HallServerProtocol.JoinRoomCmd
                    cmdObj = HallServerProtocol.JoinRoomCmd.parseFrom(clientMsg.getMsgBody());
                return cmdObj.getRoomId();
            } else if (ClubServerProtocol.ClubServerMsgCodeDef._JoinTableCmd_VALUE == clientMsg.getMsgCode()) {
                ClubServerProtocol.JoinTableCmd
                    cmdObj = ClubServerProtocol.JoinTableCmd.parseFrom(clientMsg.getMsgBody());
                return cmdObj.getRoomId();
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return -1;
    }

    /**
     * 获取游戏服务器连接
     *
     * @param roomId 房间 Id
     * @return Netty 客户端
     */
    private NettyClient getGameServerConnByRoomId(int roomId) {
        if (roomId <= 0) {
            return null;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            String strRoomAtServerId = redisCache.hget(
                RedisKeyDef.ROOM_X_PREFIX + roomId,
                RedisKeyDef.ROOM_AT_SERVER_ID
            );

            if (null == strRoomAtServerId ||
                strRoomAtServerId.isEmpty()) {
                return null;
            }

            // 获取服务器连接
            NettyClient serverConn = ServerSelector.getServerConnByServerId(
                NewServerFinder.getInstance(),
                Integer.parseInt(strRoomAtServerId)
            );

            if (null != serverConn &&
                serverConn.isReady()) {
                // 如果服务器连接是正常的,
                return serverConn;
            } else {
                // 如果服务器连接不正常,
                // 从 Redis 中删除房间所在服务器 Id,
                // 并且删除用户所在房间 Id
                redisCache.hdel(
                    RedisKeyDef.ROOM_X_PREFIX + strRoomAtServerId,
                    RedisKeyDef.ROOM_AT_SERVER_ID
                );
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }
    
    /**
     * 获取游戏服务器连接
     *
     * @param userId 用户 Id
     * @return Netty 客户端
     */
    private NettyClient getGameServerConnByUserId(final int userId) {
        if (userId <= 0) {
            return null;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 用户所在房间 Id & 房间所在服务器 Id
            String strUserAtRoomId;
            String strRoomAtServerId = null;

            // 先看看用户是否在某个房间中
            strUserAtRoomId = redisCache.hget(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_AT_ROOM_ID
            );

            if (null != strUserAtRoomId &&
                !strUserAtRoomId.isEmpty()) {
                // 如果有房间 Id,
                // 那么再看看房间所在服务器 Id
                strRoomAtServerId = redisCache.hget(
                    RedisKeyDef.ROOM_X_PREFIX + strUserAtRoomId,
                    RedisKeyDef.ROOM_AT_SERVER_ID
                );
            }

            if (null != strRoomAtServerId &&
                !strRoomAtServerId.isEmpty()) {
                // 获取服务器连接
                NettyClient serverConn = ServerSelector.getServerConnByServerId(
                    NewServerFinder.getInstance(),
                    Integer.parseInt(strRoomAtServerId)
                );

                if (null != serverConn &&
                    serverConn.isReady()) {
                    // 如果服务器连接是正常的,
                    return serverConn;
                } else {
                    // 如果服务器连接不正常,
                    // 从 Redis 中删除房间所在服务器 Id,
                    // 并且删除用户所在房间 Id
                    redisCache.hdel(
                        RedisKeyDef.ROOM_X_PREFIX + strUserAtRoomId,
                        RedisKeyDef.ROOM_AT_SERVER_ID
                    );
                    redisCache.hdel(
                        RedisKeyDef.USER_X_PREFIX + userId,
                        RedisKeyDef.USER_AT_ROOM_ID
                    );
                    return null;
                }
            }

            //
            // 如果 strRoomAtServerId 为空,
            // 也就是说可以找到 "用户所在房间 Id", 但是没有找到 "房间所在服务器 Id",
            // 那么从 Redis 中删除用户所在房间 Id
            if (null != strUserAtRoomId &&
                !strUserAtRoomId.isEmpty()) {
                redisCache.hdel(
                    RedisKeyDef.USER_X_PREFIX + userId,
                    RedisKeyDef.USER_AT_ROOM_ID
                );
                return null;
            } else {
                // 如果用户所在房间 Id 为空,
                // 则选择一个新连接
                return ServerSelector.selectServerConnByServerJobType(
                    NewServerFinder.getInstance(),
                    ServerJobTypeEnum.GAME
                );
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return null;
    }
}
