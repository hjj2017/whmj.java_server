package org.mj.bizserver.mod.club.membercenter.dao;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Objects;

/**
 * 亲友圈实体
 */
public final class ClubEntity {
    /**
     * 亲友圈 Id
     */
    private Integer _clubId;

    /**
     * 亲友圈名称
     */
    private String _clubName;

    /**
     * 创建人 ( 用户 ) Id
     */
    private Integer _creatorId;

    /**
     * 房卡数量
     */
    private Integer _roomCard;

    /**
     * 公告文字
     */
    private String _notice;

    /**
     * 创建时间
     */
    private Long _createTime;

    /**
     * 人数
     */
    private Integer _numOfPeople;

    /**
     * 当前状态
     */
    private Integer _currState;

    /**
     * 固定玩法 0
     */
    private String _fixGame0;

    /**
     * 固定玩法 1
     */
    private String _fixGame1;

    /**
     * 固定玩法 2
     */
    private String _fixGame2;

    /**
     * 固定玩法 3
     */
    private String _fixGame3;

    /**
     * 固定玩法 4
     */
    private String _fixGame4;

    /**
     * 固定玩法 5
     */
    private String _fixGame5;

    /**
     * 获取亲友圈 Id
     *
     * @return 亲友圈 Id
     */
    @JSONField(name = "clubId")
    public Integer getClubId() {
        return Objects.requireNonNullElse(_clubId, 0);
    }

    /**
     * 设置亲友圈 Id
     *
     * @param val 亲友圈 Id
     */
    public void setClubId(Integer val) {
        _clubId = val;
    }

    /**
     * 获取亲友圈名称
     *
     * @return 亲友圈名称
     */
    @JSONField(name = "clubName")
    public String getClubName() {
        return Objects.requireNonNullElse(_clubName, "");
    }

    /**
     * 设置亲友圈名称
     *
     * @param val 亲友圈名称
     */
    public void setClubName(String val) {
        _clubName = val;
    }

    /**
     * 获取创建人 ( 用户 ) Id
     *
     * @return 创建人 ( 用户 ) Id
     */
    @JSONField(name = "creatorId")
    public Integer getCreatorId() {
        return Objects.requireNonNullElse(_creatorId, 0);
    }

    /**
     * 设置创建人  ( 用户 ) Id
     *
     * @param val 创建人  ( 用户 ) Id
     */
    public void setCreatorId(Integer val) {
        _creatorId = val;
    }

    /**
     * 获取房卡数量
     *
     * @return 房卡数量
     */
    @JSONField(name = "roomCard")
    public Integer getRoomCard() {
        return Objects.requireNonNullElse(_roomCard, 0);
    }

    /**
     * 设置房卡数量
     *
     * @param val 房卡数量
     */
    public void setRoomCard(Integer val) {
        _roomCard = val;
    }

    /**
     * 获取公告文字
     *
     * @return 公告文字
     */
    @JSONField(name = "notice")
    public String getNotice() {
        return Objects.requireNonNullElse(_notice, "");
    }

    /**
     * 设置公告文字
     *
     * @param val 公告文字
     */
    public void setNotice(String val) {
        _notice = val;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    @JSONField(name = "createTime")
    public Long getCreateTime() {
        return Objects.requireNonNullElse(_createTime, 0L);
    }

    /**
     * 设置创建时间
     *
     * @param val 创建时间
     */
    public void setCreateTime(Long val) {
        _createTime = val;
    }

    /**
     * 获取人数
     *
     * @return 人数
     */
    @JSONField(name = "numOfPeople")
    public Integer getNumOfPeople() {
        return Objects.requireNonNullElse(_numOfPeople, 0);
    }

    /**
     * 设置人数
     *
     * @param val 人数
     */
    public void setNumOfPeople(Integer val) {
        _numOfPeople = val;
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    @JSONField(name = "currState")
    public Integer getCurrState() {
        return Objects.requireNonNullElse(_currState, 0);
    }

    /**
     * 设置当前状态
     *
     * @param val 当前状态
     */
    public void setCurrState(Integer val) {
        _currState = val;
    }

    /**
     * 获取固定玩法 0
     *
     * @return 固定玩法 0
     */
    @JSONField(name = "fixGame0")
    public String getFixGame0() {
        return _fixGame0;
    }

    /**
     * 设置固定玩法 0
     *
     * @param val 固定玩法 0
     */
    public void setFixGame0(String val) {
        _fixGame0 = val;
    }

    /**
     * 获取固定玩法 1
     *
     * @return 固定玩法 1
     */
    @JSONField(name = "fixGame1")
    public String getFixGame1() {
        return _fixGame1;
    }

    /**
     * 设置固定玩法 1
     *
     * @param val 固定玩法 1
     */
    public void setFixGame1(String val) {
        _fixGame1 = val;
    }

    /**
     * 获取固定玩法 2
     *
     * @return 固定玩法 2
     */
    @JSONField(name = "fixGame2")
    public String getFixGame2() {
        return _fixGame2;
    }

    /**
     * 设置固定玩法 2
     *
     * @param val 固定玩法 2
     */
    public void setFixGame2(String val) {
        _fixGame2 = val;
    }

    /**
     * 获取固定玩法 3
     *
     * @return 固定玩法 3
     */
    @JSONField(name = "fixGame3")
    public String getFixGame3() {
        return _fixGame3;
    }

    /**
     * 设置固定玩法 3
     *
     * @param val 固定玩法 3
     */
    public void setFixGame3(String val) {
        _fixGame3 = val;
    }

    /**
     * 获取固定玩法 4
     *
     * @return 固定玩法 4
     */
    @JSONField(name = "fixGame4")
    public String getFixGame4() {
        return _fixGame4;
    }

    /**
     * 设置固定玩法 4
     *
     * @param val 固定玩法 4
     */
    public void setFixGame4(String val) {
        _fixGame4 = val;
    }

    /**
     * 获取固定玩法 5
     *
     * @return 固定玩法 5
     */
    @JSONField(name = "fixGame5")
    public String getFixGame5() {
        return _fixGame5;
    }

    /**
     * 设置固定玩法 5
     *
     * @param val 固定玩法 5
     */
    public void setFixGame5(String val) {
        _fixGame5 = val;
    }

    /**
     * 获取固定玩法 X
     *
     * @param index 索引
     * @return 固定玩法 X
     */
    @JSONField(serialize = false)
    public String getFixGameX(int index) {
        switch (index) {
            case 0: return getFixGame0();
            case 1: return getFixGame1();
            case 2: return getFixGame2();
            case 3: return getFixGame3();
            case 4: return getFixGame4();
            case 5: return getFixGame5();
        }

        return null;
    }
}
