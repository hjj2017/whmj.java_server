package org.mj.proxyserver.nobody;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.bizserver.def.RedisKeyDef;
import org.mj.comm.util.DLock;
import org.mj.comm.util.RedisXuite;
import org.mj.proxyserver.ProxyServer;
import org.mj.proxyserver.foundation.ClientChannelGroup;
import org.mj.proxyserver.foundation.ClientMsgHandler;
import org.mj.proxyserver.foundation.ClientMsgSemiFinished;
import org.mj.proxyserver.foundation.IdSetterGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * 检票命令处理器,
 * XXX 注意: 检票过程是在 proxyServer ( 代理服务器 ) 上完成的!
 * 而且检票过程中会检查用户是否同时连接其他代理服务器?
 * 如果存在上述情况,
 * 那么会广播 CONNECTION_TRANSFER_NOTICE,
 * 令用户断开连接...
 */
public class CheckInTicketCmdHandler extends ChannelInboundHandlerAdapter {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(CheckInTicketCmdHandler.class);

    /**
     * 类参数构造器
     */
    public CheckInTicketCmdHandler() {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgObj) {
        try {
            if (null == ctx ||
                !(msgObj instanceof ClientMsgSemiFinished)) {
                // 如果接到的不是客户端半成品消息,
                super.channelRead(ctx, msgObj);
                return;
            }

            // 获取客户端消息
            ClientMsgSemiFinished clientMsg = (ClientMsgSemiFinished) msgObj;

            if (CommProtocol.CommMsgCodeDef._CheckInTicketCmd_VALUE != clientMsg.getMsgCode()) {
                // 如果接到的不是检票命令,
                super.channelRead(ctx, msgObj);
                return;
            }

            // 创建检票命令
            CommProtocol.CheckInTicketCmd
                newCmd = CommProtocol.CheckInTicketCmd.parseFrom(clientMsg.getMsgBody());

            // 处理消息
            handle(ctx, newCmd);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 处理检票命令对象 ( 正式 )
     *
     * @param ctx    信道处理器上下文
     * @param cmdObj 检票命令对象
     */
    private void handle(ChannelHandlerContext ctx, CommProtocol.CheckInTicketCmd cmdObj) {
        if (null == ctx ||
            null == cmdObj) {
            return;
        }

        // 获取用户 Id 和票据
        final int userId = cmdObj.getUserId();
        final String ticket = cmdObj.getTicket();

        if (userId <= 0 ||
            null == ticket ||
            ticket.isEmpty()) {
            LOGGER.error("登陆票据为空, userId = {}, ticket = {}", userId, ticket);
            ctx.disconnect();
            return;
        }

        // 创建分布式锁
        final DLock newLocker = DLock.newLock("check_in_ticket?user_id=" + userId);

        //
        // 在检票之前先加锁,
        // 避免多个代理服务器同时验证一张票据!
        // 如果一个玩家通过不同的客户端连接到不同的代理服务器,
        // 但是登陆身份是一样的,
        // 就会出现这个问题...
        if (null == newLocker ||
            !newLocker.tryLock(5000)) {
            LOGGER.error("检票时加锁失败, userId = {}", userId);
            ctx.disconnect();
            return;
        }

        try (Jedis redisCache = RedisXuite.getRedisCache()) {
            // 获取期望值并清空数据
            String expectId = redisCache.get(RedisKeyDef.TICKET_X_PREFIX + ticket);
            redisCache.del(RedisKeyDef.TICKET_X_PREFIX + ticket);

            if (!String.valueOf(userId).equals(expectId)) {
                LOGGER.error(
                    "检票失败, userId = {}",
                    userId
                );
                ctx.disconnect();
                return;
            }

            if (renewConn(ctx, userId, redisCache)) {
                // 构建检票结果
                CommProtocol.CheckInTicketResult r = CommProtocol.CheckInTicketResult.newBuilder()
                    .setUserId(userId)
                    .setSuccezz(true)
                    .build();

                // 发送检票结果
                ctx.writeAndFlush(r);
            }
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            // 释放异步锁
            newLocker.release();
        }
    }

    /**
     * 重建连接,
     * 在重建连接的过程中, 会根据用户 Id 踢掉重复连接
     *
     * @param ctx        信道处理器上下文
     * @param userId     用户 Id
     * @param redisCache Redis 缓存, 可以为空
     * @return true = 重建成功, false = 重建失败
     */
    static boolean renewConn(ChannelHandlerContext ctx, int userId, Jedis redisCache) {
        if (null == ctx ||
            userId <= 0) {
            return false;
        }

        if (null == redisCache) {
            try (Jedis newCache = RedisXuite.getRedisCache()) {
                // 获取新的 Redis 之后重新执行
                return renewConn(
                    ctx, userId, newCache
                );
            }
        }

        try {
            // 如果已经标记所在代理服务器,
            // 那么就删除这个标记!
            Long delOther = redisCache.hdel(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_AT_PROXY_SERVER_ID,
                RedisKeyDef.USER_REMOTE_SESSION_ID
            );

            if (null != delOther &&
                delOther > 0) {
                // 如果删除成功,
                // 则说明用户已经在其他代理服务器上建立连接,
                // 通过 RECONNECT_USER_NOTICE 通知其断开连接!
                // 但是当前连接不要断开...
                try (Jedis redisPubSub = RedisXuite.getRedisPubSub()) {
                    // 无论是否连接到其他代理服务器,
                    // 都通知其他代理服务器断开客户端连接...
                    JSONObject joNotice = new JSONObject();
                    joNotice.put("newProxyServerId", ProxyServer.getId());
                    joNotice.put("userId", userId);

                    redisPubSub.publish(
                        PubSubChannelDef.CONNECTION_TRANSFER_NOTICE,
                        joNotice.toJSONString()
                    );
                }

                LOGGER.warn("用户已经在其他代理服务器上建立连接, 需要断开连接! userId = {}", userId);
            }

            // 获取本地服务器上的连接
            Channel oldCh = ClientChannelGroup.getByUserId(userId);

            if (null != oldCh &&
                oldCh != ctx.channel()) {
                // 如果本服务器上已有不同连接,
                LOGGER.warn("连接转移! userId = {}", userId);
                ClientMsgHandler msgHandler = oldCh.pipeline().get(ClientMsgHandler.class);
                msgHandler.putConnAlreadyTransfer(true);

                CommProtocol.KickOutUserResult resultMsg = CommProtocol.KickOutUserResult.newBuilder()
                    .setReason("已经连接到其他服务器")
                    .build();

                oldCh.writeAndFlush(resultMsg);
                oldCh.disconnect().sync().await(2, TimeUnit.SECONDS);
            }

            LOGGER.info("检票成功, 设置用户 Id = {}", userId);

            // 设置用户 Id
            IdSetterGetter.putUserId(ctx, userId);
            ClientChannelGroup.relative(userId, IdSetterGetter.getSessionId(ctx));

            // 标记所在代理服务器 Id
            redisCache.hset(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_AT_PROXY_SERVER_ID,
                String.valueOf(ProxyServer.getId())
            );

            // 标记远程会话 Id
            redisCache.hset(
                RedisKeyDef.USER_X_PREFIX + userId,
                RedisKeyDef.USER_REMOTE_SESSION_ID,
                String.valueOf(IdSetterGetter.getSessionId(ctx))
            );

            return true;
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }

        return false;
    }
}
