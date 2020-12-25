package org.mj.bizserver.mod.userlogin;

/**
 * 用户登录业务逻辑
 */
public final class UserLoginBizLogic implements
    UserLoginBizLogic$doUserLogin {
    /**
     * 单例对象
     */
    static private final UserLoginBizLogic _instance = new UserLoginBizLogic();

    /**
     * 类默认构造器
     */
    private UserLoginBizLogic() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public UserLoginBizLogic getInstance() {
        return _instance;
    }
}
