package org.mj.bizserver.mod.club.adminctrl;

/**
 * 管理员控制器业务逻辑
 */
public final class AdminCtrlBizLogic implements
    AdminCtrlBizLogic$approvalToJoin,
    AdminCtrlBizLogic$changeRole,
    AdminCtrlBizLogic$createClub,
    AdminCtrlBizLogic$dismissAMember,
    AdminCtrlBizLogic$exchangeRoomCard,
    AdminCtrlBizLogic$modifyFixGameX {

    /**
     * 单例对象
     */
    static private final AdminCtrlBizLogic _instance = new AdminCtrlBizLogic();

    /**
     * 私有化类默认构造器
     */
    private AdminCtrlBizLogic() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public AdminCtrlBizLogic getInstance() {
        return _instance;
    }
}
