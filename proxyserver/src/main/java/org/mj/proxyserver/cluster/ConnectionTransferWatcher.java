package org.mj.proxyserver.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import org.mj.bizserver.allmsg.CommProtocol;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.comm.pubsub.MySubscriber;
import org.mj.proxyserver.ProxyServer;
import org.mj.proxyserver.foundation.ClientChannelGroup;
import org.mj.proxyserver.foundation.ClientMsgHandler;
import org.mj.proxyserver.foundation.IdSetterGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 连接转移监听
 */
public class ConnectionTransferWatcher implements MySubscriber.IMsgHandler {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ConnectionTransferWatcher.class);

    @Override
    public void handle(String ch, String strMsg) {
        if (!PubSubChannelDef.CONNECTION_TRANSFER_NOTICE.equals(ch) ||
            null == strMsg ||
            !strMsg.startsWith("{")) {
            return;
        }

        JSONObject joMsg = JSON.parseObject(strMsg);

        if (ProxyServer.getId().equals(joMsg.getString("newProxyServerId"))) {
            // 如果重新连接到当前服务器,
            // 则不做任何处理...
            return;
        }

        // 获取用户 Id
        int userId = joMsg.getIntValue("userId");
        // 移除客户端信道
        Channel clientCh = ClientChannelGroup.removeByUserId(userId);

        if (null != clientCh) {
            LOGGER.warn(
                "客户端断开连接, sessionId = {}, userId = {}",
                IdSetterGetter.getSessionId(clientCh),
                userId
            );

            try {
                ClientMsgHandler msgHandler = clientCh.pipeline().get(ClientMsgHandler.class);
                msgHandler.putConnAlreadyTransfer(true);

                CommProtocol.KickOutUserResult resultMsg = CommProtocol.KickOutUserResult.newBuilder()
                    .setReason("已经连接到其他服务器")
                    .build();

                clientCh.writeAndFlush(resultMsg);
                clientCh.disconnect().sync().await(2, TimeUnit.SECONDS);
            } catch (Exception ex) {
                // 记录错误日志
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
