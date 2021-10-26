package org.mj.bizserver.def;

import org.mj.bizserver.base.BizResultWrapper;

/**
 * 错误定义
 */
public enum ErrorEnum {
    PARAM_ERROR(1, "参数错误"),
    OPERATING_TOO_FREQUENTLY(2, "操作过于频繁"),
    SERVICE_NOT_INITED(3, "服务尚未初始化"),
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),

    // 账户相关
    PASSPORT__AUTH_PROC_IS_NULL(101, "登录过程为空"),
    PASSPORT__DO_AUTH_FAIL(102, "授权过程失败"),
    PASSPORT__PWD_INCORRECT(103, "密码错误"),
    PASSPORT__USER_DETAILZ_IS_NULL(104, "用户详情为空"),

    // 亲友圈相关
    CLUB__THE_CLUB_NOT_EXIST(301, "老友圈不存在"),
    CLUB__ALREADY_JOINED(302, "已经加入该老友圈"),
    CLUB__JOINED_CLUB_TOO_MANY(303, "已经加入的老友圈数量过多"),
    CLUB__DECLINE(304, "已被圈主拒绝"),
    CLUB__YOU_NOT_ADMIN(305, "您不是亲友圈管理员"),
    CLUB__YOU_NOT_SUPER_ADMIN(306, "您不是亲友圈超级管理员"),
    CLUB__EXCHANGE_ROOM_CARD_BUT_NOT_ENOUGH(307, "亲友圈充值房卡失败, 您的房卡数量不足"),

    // 游戏相关
    GAME__GEN_ROOM_ID_FAIL(4001, "生成房间 Id 失败"),
    GAME__OTHER_ROOM_HAS_BEEN_JOINED(4002, "已加入其他房间"),
    GAME__ROOM_NOT_EXIST(4003, "房间不存在"),
    GAME__ROOM_CURR_USER_IS_DENIED(4004, "房主已拒绝您加入房间"),
    GAME__THE_ROOM_IS_FULL(4005, "房间已经满员"),
    GAME__NOT_AT_THE_ROOM(4006, "不在当前房间中"),
    GAME__NOT_AT_ANY_ROOM(4007, "不在任何房间中"),
    GAME__ROOM_CARD_NOT_ENOUGH(4008, "房卡数量不足"),
    GAME__ROOM_RULE_SETTING_HAS_ERROR(4009, "房间规则设置错误"),
    GAME__IS_OFFICIAL_STARTED(4010, "游戏已经正式开始"),
    GAME__IS_NOT_ROOM_OWNER(4011, "当前玩家不是房主"),
    GAME__ROOM_OWNER_CAN_NOT_QUIT(4012, "房主不能退出"),
    ;

    /**
     * 错误编号
     */
    private final int _errorCode;

    /**
     * 错误消息
     */
    private final String _errorMsg;

    /**
     * 类参数构造器
     *
     * @param errorCode 错误编码
     * @param errorMsg  错误消息
     */
    ErrorEnum(int errorCode, String errorMsg) {
        _errorCode = errorCode;
        _errorMsg = errorMsg;
    }

    /**
     * 获取错误编号
     *
     * @return 错误编号
     */
    public int getErrorCode() {
        return _errorCode;
    }

    /**
     * 获取错误消息
     *
     * @return 错误消息
     */
    public String getErrorMsg() {
        return _errorMsg;
    }

    /**
     * 填充业务结果
     *
     * @param resultX 业务结果
     */
    public void fillResultX(BizResultWrapper<?> resultX) {
        if (null != resultX) {
            resultX.setErrorCode(getErrorCode());
            resultX.setErrorMsg(getErrorMsg());
        }
    }
}
