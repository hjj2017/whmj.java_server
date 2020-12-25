package org.mj.bizserver.def;

/**
 * 发布订阅频道定义
 */
public final class PubSubChannelDef {
    /**
     * 新服务器进来
     */
    static public final String NEW_SERVER_COME_IN = "new_server_come_in";

    /**
     * 离线用户通知,
     * 一般是由于客户端主动断开与代理服务器的连接
     */
    static public final String OFFLINE_USER_NOTICE = "offline_user_notice";

    /**
     * 踢除用户通知,
     * 一般是由代理服务器或内部服务器主动断开与客户端的连接
     */
    static public final String KICK_OUT_USER_NOTICE = "kick_out_user_notice";

    /**
     * 连接转移通知,
     * 一般发生在旧连接没断开, 新连接又进来的情况...
     */
    static public final String CONNECTION_TRANSFER_NOTICE = "connection_transfer_notice";

    /**
     * 亲友圈牌桌变化通知,
     * 添加、更新、删除使用的都是这个频道
     */
    static public final String A_CLUB_TABLE_CHANGED = "a_club_table_changed";
}
