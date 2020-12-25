package org.mj.proxyserver.cluster;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.comm.pubsub.MySubscriber;
import org.mj.proxyserver.ProxyServer;
import org.mj.proxyserver.foundation.ClientChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 踢除用户监控
 */
public class KickOutUserWatcher implements MySubscriber.IMsgHandler {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(KickOutUserWatcher.class);

    @Override
    public void handle(String ch, String strMsg) {
        if (!PubSubChannelDef.KICK_OUT_USER_NOTICE.equals(ch) ||
            null == strMsg ||
            strMsg.isEmpty()) {
            return;
        }

        JSONObject joNotice = JSONObject.parseObject(strMsg);

        if (ProxyServer.getId() == joNotice.getIntValue("fromServerId")) {
            // 如果是本服务器发出的通知,
            return;
        }

        // 记录警告日志
        LOGGER.warn("收到并处理踢除用户的通知, msg = {}", strMsg);

        // 获取用户 Id
        int userId = joNotice.getIntValue("userId");

        // 获取客户端信道
        Channel clientCh = ClientChannelGroup.removeByUserId(userId);

        if (null == clientCh) {
            return;
        }

        try {
            // 记录警告日志
            LOGGER.warn("令用户断开连接, userId = {}", userId);

            CommProtocol.KickOutUserResult resultMsg = CommProtocol.KickOutUserResult.newBuilder()
                .setReason("强制断开用户连接")
                .build();

            clientCh.writeAndFlush(resultMsg);
            clientCh.disconnect().sync().await(2, TimeUnit.SECONDS);
        } catch (Exception ex) {
            // 记录错误日志
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
