package org.mj.proxyserver.cluster;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.allmsg.ClubServerProtocol;
import org.mj.bizserver.def.PubSubChannelDef;
import org.mj.comm.pubsub.MySubscriber;
import org.mj.proxyserver.foundation.ClientChannelGroup;
import org.mj.proxyserver.nobody.router.RouteTable;

/**
 * 亲友圈牌桌变化监听者
 */
public class AClubTableChangedListener implements MySubscriber.IMsgHandler {
    @Override
    public void handle(String channel, String strMsg) {
        if (!PubSubChannelDef.A_CLUB_TABLE_CHANGED.equals(channel) ||
            null == strMsg ||
            strMsg.isEmpty()) {
            return;
        }

        JSONObject jsonObj = JSONObject.parseObject(strMsg);

        // 获取亲友圈 Id
        final int clubId = jsonObj.getIntValue("clubId");

        final ClubServerProtocol.AClubTableChangedBroadcast
            msgObj = ClubServerProtocol.AClubTableChangedBroadcast.newBuilder()
            .setClubId(clubId)
            .setTableSeqNum(jsonObj.getIntValue("tableSeqNum"))
            .setRoomId(jsonObj.getIntValue("roomId"))
            .build();

        ClientChannelGroup.forEachChannel((ch) -> {
            if (null == ch) {
                return;
            }

            // 获取路由表
            RouteTable rt = RouteTable.getOrCreate(ch);

            if (rt.getFocusClubId() != clubId) {
                // 如果没有关注当前亲友圈,
                // 则直接退出...
                return;
            }

            // 告知客户端亲友圈牌桌有变化
            ch.writeAndFlush(msgObj);
        });
    }
}
