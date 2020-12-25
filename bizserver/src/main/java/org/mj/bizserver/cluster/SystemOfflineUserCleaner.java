package org.mj.bizserver.cluster;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.comm.pubsub.MySubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 离线用户清理员
 */
public class SystemOfflineUserCleaner implements MySubscriber.IMsgHandler {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(SystemOfflineUserCleaner.class);

    @Override
    public void handle(String ch, String strMsg) {
        if (!PubSubChannelDef.OFFLINE_USER_NOTICE.equals(ch) ||
            null == strMsg ||
            strMsg.isEmpty()) {
            return;
        }

        // 获取离线用户
        JSONObject joUser = JSONObject.parseObject(strMsg);
        int remoteSessionId = joUser.getIntValue("remoteSessionId");
        int userId = joUser.getIntValue("userId");

        LOGGER.info(
            "收到用户离线通知, remoteSessionId = {}, userId = {}",
            remoteSessionId,
            userId
        );
    }
}
