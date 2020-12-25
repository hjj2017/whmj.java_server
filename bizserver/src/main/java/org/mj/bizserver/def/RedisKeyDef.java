package org.mj.bizserver.def;

/**
 * Redis 关键字定义
 */
public final class RedisKeyDef {
    /**
     * 服务器
     */
    static public final String SERVER_X_PREFIX = "keyval#server_";

    /**
     * 分布式锁
     */
    static public final String DLOCK_X_PREFIX = "keyval#dlock_";

    /**
     * 短信验证码
     */
    static public final String SMS_AUTH_CODE_PREFIX = "keyval#sms_auth_code_";

    /**
     * 用户 Id 泵
     */
    static public final String USER_ID_PUMP = "list#user_id_pump";

    /**
     * 票据
     */
    static public final String TICKET_X_PREFIX = "keyval#ticket_";

    /**
     * 用户
     */
    static public final String USER_X_PREFIX = "hash#user_";

    /**
     * 用户所在代理服务器 Id
     */
    static public final String USER_AT_PROXY_SERVER_ID = "user_at_proxy_server_id";

    /**
     * 用户远程会话 Id
     */
    static public final String USER_REMOTE_SESSION_ID = "user_remote_session_id";

    /**
     * 用户详情
     */
    static public final String USER_DETAILZ = "user_detailz";

    /**
     * 用户所在房间 Id
     *
     * @see #USER_X_PREFIX
     */
    static public final String USER_AT_ROOM_ID = "user_at_room_id";

    /**
     * 用户已经加入的亲友圈 Id 数组
     */
    static public final String USER_JOINED_CLUB_ID_ARRAY = "user_joined_club_id_array";

    /**
     * 房间
     */
    static public final String ROOM_X_PREFIX = "hash#room_";

    /**
     * 房间所在服务器 Id
     *
     * @see #ROOM_X_PREFIX
     */
    static public final String ROOM_AT_SERVER_ID = "room_at_server_id";

    /**
     * 房间详情
     *
     * @see #ROOM_X_PREFIX
     */
    static public final String ROOM_DETAILZ = "room_detailz";

    /**
     * 亲友圈 Id 泵
     */
    static public final String CLUB_ID_PUMP = "list#club_id_pump";

    /**
     * 亲友圈
     */
    static public final String CLUB_X_PREFIX = "hash#club_";

    /**
     * 亲友详情
     *
     * @see #CLUB_X_PREFIX
     */
    static public final String CLUB_DETAILZ = "club_detailz";

    /**
     * 游戏中的数量 ( 桌数 )
     *
     * @see #CLUB_X_PREFIX
     */
    static public final String CLUB_NUM_OF_GAMING = "club_num_of_gaming";

    /**
     * 等待数量 ( 桌数 )
     *
     * @see #CLUB_X_PREFIX
     */
    static public final String CLUB_NUM_OF_WAITING = "club_num_of_waiting";

    /**
     * 亲友圈成员 Id 数组
     *
     * @see #CLUB_X_PREFIX
     */
    static public final String CLUB_MEMBER_ID_ARRAY = "club_member_id_array";

    /**
     * 亲友圈成员信息前缀
     *
     * @see #CLUB_X_PREFIX
     */
    static public final String CLUB_MEMBER_INFO_X_PREFIX = "club_member_info_";

    /**
     * 亲友圈牌桌前缀,
     * XXX 注意: 通过桌号作为 key!
     * 例如:
     * "club_table_1", 对应 1 号桌.
     * "club_table_2", 对应 2 号桌.
     * 如果想拿到亲友圈 ( Id = 123456 ) 中 1 号桌所对应的房间 Id, 请使用:
     * hget "hash#club_123456" "club_table_1"
     *
     * @see #CLUB_X_PREFIX
     */
    static public final String CLUB_TABLE_X_PREFIX = "club_table_";

    /**
     * 私有化类默认构造器
     */
    private RedisKeyDef() {
    }
}
