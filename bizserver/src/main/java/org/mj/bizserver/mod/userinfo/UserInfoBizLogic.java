package org.mj.bizserver.mod.userinfo;

/**
 * 用户信息业务逻辑
 */
public final class UserInfoBizLogic implements
    UserInfoBizLogic$costRoomCard,
    UserInfoBizLogic$getUserDetailzByUserId {
    /**
     * 单例对象
     */
    static private final UserInfoBizLogic _instance = new UserInfoBizLogic();

    /**
     * 类默认构造器
     */
    private UserInfoBizLogic() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public UserInfoBizLogic getInstance() {
        return _instance;
    }
}
