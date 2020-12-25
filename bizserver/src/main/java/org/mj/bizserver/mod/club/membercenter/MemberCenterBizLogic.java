package org.mj.bizserver.mod.club.membercenter;

/**
 * 客户中心业务逻辑
 */
public final class MemberCenterBizLogic implements
    MemberCenterBizLogic$costRoomCard,
    MemberCenterBizLogic$getClubDetailz,
    MemberCenterBizLogic$getJoinedClubList,
    MemberCenterBizLogic$getMemberInfoList,
    MemberCenterBizLogic$getTableDetailz,
    MemberCenterBizLogic$getTableList,
    MemberCenterBizLogic$joinClub,
    MemberCenterBizLogic$quitClub {
    /**
     * 单例对象
     */
    static private final MemberCenterBizLogic _instance = new MemberCenterBizLogic();

    /**
     * 可加入亲友圈的数量上限
     */
    static private final int MAX_CAN_JOINED_CLUB_COUNT = 5;

    /**
     * 私有化类默认构造器
     */
    private MemberCenterBizLogic() {
    }

    /**
     * 获取单例对象
     *
     * @return 单例对象
     */
    static public MemberCenterBizLogic getInstance() {
        return _instance;
    }

    /**
     * 获取可以加入亲友圈的数量上限
     *
     * @return 可以加入亲友圈的数量上限
     */
    public int getMaxCanJoinedClubCount() {
        return MAX_CAN_JOINED_CLUB_COUNT;
    }
}
