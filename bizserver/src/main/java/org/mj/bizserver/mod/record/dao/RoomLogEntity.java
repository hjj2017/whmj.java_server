package org.mj.bizserver.mod.record.dao;

import org.mj.comm.util.DateTimeUtil;

import java.util.Objects;

/**
 * 房间日志实体
 */
public class RoomLogEntity {
    /**
     * 房间 UUId
     */
    private String _roomUUId;

    /**
     * 房间 Id
     */
    private Integer _roomId;

    /**
     * 房主用户 Id
     */
    private Integer _ownerId;

    /**
     * 亲友圈 Id
     */
    private Integer _clubId;

    /**
     * 创建时间
     */
    private Long _createTime;

    /**
     * 结束时间
     */
    private Long _overTime;

    /**
     * 游戏类型 0
     */
    private Integer _gameType0;

    /**
     * 游戏类型 1
     */
    private Integer _gameType1;

    /**
     * 规则设置
     */
    private String _ruleSetting;

    /**
     * 所有玩家
     */
    private String _allPlayer;

    /**
     * 所有总分
     */
    private String _allTotalScore;

    /**
     * 消费房卡数量
     */
    private Integer _costRoomCard;

    /**
     * 实际局数
     */
    private Integer _actualRoundCount;

    /**
     * 用户 Id 0
     */
    private Integer _userId0;

    /**
     * 用户 Id 1
     */
    private Integer _userId1;

    /**
     * 用户 Id 2
     */
    private Integer _userId2;

    /**
     * 用户 Id 3
     */
    private Integer _userId3;

    /**
     * 用户 Id 4
     */
    private Integer _userId4;

    /**
     * 用户 Id 5
     */
    private Integer _userId5;

    /**
     * 当前状态
     */
    private Integer _currState;

    /**
     * 本周表名称前缀
     */
    private String _thisWeekTableNamePrefix;

    /**
     * 下周表名称前缀
     */
    private String _nextWeekTableNamePrefix;

    /**
     * 获取房间 UUId
     *
     * @return 房间 UUId
     */
    public String getRoomUUId() {
        return _roomUUId;
    }

    /**
     * 设置房间 UUId
     *
     * @param val 房间 UUId
     */
    public void setRoomUUId(String val) {
        _roomUUId = val;
    }

    /**
     * 获取房间 Id
     *
     * @return 房间 Id
     */
    public Integer getRoomId() {
        return Objects.requireNonNullElse(_roomId, -1);
    }

    /**
     * 设置房间 Id
     *
     * @param val 房间 Id
     */
    public void setRoomId(Integer val) {
        _roomId = val;
    }

    /**
     * 获取房主用户 Id
     *
     * @return 房主用户 Id
     */
    public Integer getOwnerId() {
        return Objects.requireNonNullElse(_ownerId, -1);
    }

    /**
     * 设置房主用户 Id
     *
     * @param val 房主用户 Id
     */
    public void setOwnerId(Integer val) {
        _ownerId = val;
    }

    /**
     * 获取亲友圈 Id
     *
     * @return 亲友圈 Id
     */
    public Integer getClubId() {
        return Objects.requireNonNullElse(_clubId, -1);
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
     * 获取房间创建时间
     *
     * @return 房间创建时间
     */
    public Long getCreateTime() {
        return Objects.requireNonNullElse(_createTime, -1L);
    }

    /**
     * 设置房间创建时间
     *
     * @param val 房间创建时间
     */
    public void setCreateTime(Long val) {
        _createTime = val;
    }

    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    public Long getOverTime() {
        return Objects.requireNonNullElse(_overTime, -1L);
    }

    /**
     * 设置结束时间
     *
     * @param val 结束时间
     */
    public void setOverTime(Long val) {
        _overTime = val;
    }

    /**
     * 获取游戏类型 0
     *
     * @return 游戏类型 0
     */
    public Integer getGameType0() {
        return Objects.requireNonNullElse(_gameType0, -1);
    }

    /**
     * 设置游戏类型 0
     *
     * @param val 游戏类型 0
     */
    public void setGameType0(Integer val) {
        _gameType0 = val;
    }

    /**
     * 获取游戏类型 1
     *
     * @return 游戏类型 1
     */
    public Integer getGameType1() {
        return Objects.requireNonNullElse(_gameType1, -1);
    }

    /**
     * 设置游戏类型 1
     *
     * @param val 游戏类型 1
     */
    public void setGameType1(Integer val) {
        _gameType1 = val;
    }

    /**
     * 获取规则设置 ( JSON 字符串 )
     *
     * @return 规则设置
     */
    public String getRuleSetting() {
        return _ruleSetting;
    }

    /**
     * 设置规则设置 ( JSON 字符串 )
     *
     * @param val 规则设置
     */
    public void setRuleSetting(String val) {
        _ruleSetting = val;
    }

    /**
     * 获取所有玩家 ( JSON 字符串 )
     *
     * @return 所有玩家
     */
    public String getAllPlayer() {
        return _allPlayer;
    }

    /**
     * 设置所有玩家 ( JSON 字符串 )
     *
     * @param val 所有玩家
     */
    public void setAllPlayer(String val) {
        _allPlayer = val;
    }

    /**
     * 获取所有总分数 ( JSON 字符串 )
     *
     * @return 所有总分数
     */
    public String getAllTotalScore() {
        return _allTotalScore;
    }

    /**
     * 设置所有总分数 ( JSON 字符串 )
     *
     * @param val 所有总分数
     */
    public void setAllTotalScore(String val) {
        _allTotalScore = val;
    }

    /**
     * 获取消费房卡数量
     *
     * @return 消费房卡数量
     */
    public Integer getCostRoomCard() {
        return _costRoomCard;
    }

    /**
     * 设置消费房卡数量
     *
     * @param val 消费房卡数量
     */
    public void setCostRoomCard(Integer val) {
        _costRoomCard = val;
    }

    /**
     * 获取实际局数
     *
     * @return 实际局数
     */
    public Integer getActualRoundCount() {
        return _actualRoundCount;
    }

    /**
     * 设置实际局数
     *
     * @param val 实际局数
     */
    public void setActualRoundCount(Integer val) {
        _actualRoundCount = val;
    }

    /**
     * 获取用户 Id 0
     *
     * @return 用户 Id 0
     */
    public Integer getUserId0() {
        return Objects.requireNonNullElse(_userId0, -1);
    }

    /**
     * 设置用户 Id 0
     *
     * @param val 用户 Id 0
     */
    public void setUserId0(Integer val) {
        _userId0 = val;
    }

    /**
     * 获取用户 Id 1
     *
     * @return 用户 Id 1
     */
    public Integer getUserId1() {
        return Objects.requireNonNullElse(_userId1, -1);
    }

    /**
     * 设置用户 Id 1
     *
     * @param val 用户 Id 1
     */
    public void setUserId1(Integer val) {
        _userId1 = val;
    }

    /**
     * 获取用户 Id 2
     *
     * @return 用户 Id 2
     */
    public Integer getUserId2() {
        return Objects.requireNonNullElse(_userId2, -1);
    }

    /**
     * 设置用户 Id 2
     *
     * @param val 用户 Id 2
     */
    public void setUserId2(Integer val) {
        _userId2 = val;
    }

    /**
     * 获取用户 Id 3
     *
     * @return 用户 Id 3
     */
    public Integer getUserId3() {
        return Objects.requireNonNullElse(_userId3, -1);
    }

    /**
     * 设置用户 Id 3
     *
     * @param val 用户 Id 3
     */
    public void setUserId3(Integer val) {
        _userId3 = val;
    }

    /**
     * 获取用户 Id 4
     *
     * @return 用户 Id 4
     */
    public Integer getUserId4() {
        return Objects.requireNonNullElse(_userId4, -1);
    }

    /**
     * 设置用户 Id 4
     *
     * @param val 用户 Id 4
     */
    public void setUserId4(Integer val) {
        _userId4 = val;
    }

    /**
     * 获取用户 Id 5
     *
     * @return 用户 Id 5
     */
    public Integer getUserId5() {
        return Objects.requireNonNullElse(_userId5, -1);
    }

    /**
     * 设置用户 Id 5
     *
     * @param val 用户 Id 5
     */
    public void setUserId5(Integer val) {
        _userId5 = val;
    }

    /**
     * 设置用户 Id
     *
     * @param atIndex 索引位置
     * @param userId  用户 Id
     */
    public void putUserIdX(int atIndex, int userId) {
        switch (atIndex) {
            case 0:
                setUserId0(userId);
                break;

            case 1:
                setUserId1(userId);
                break;

            case 2:
                setUserId2(userId);
                break;

            case 3:
                setUserId3(userId);
                break;

            case 4:
                setUserId4(userId);
                break;

            case 5:
                setUserId5(userId);
                break;

            default:
                break;
        }
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public Integer getCurrState() {
        return Objects.requireNonNullElse(_currState, -1);
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
     * 获取本周表名称前缀
     *
     * @return 表名称前缀, 格式 = yyyyMMdd
     */
    public String getThisWeekTableNamePrefix() {
        if (null == _thisWeekTableNamePrefix) {
            _thisWeekTableNamePrefix = DateTimeUtil.getMondayDateStr(_createTime);
        }

        return _thisWeekTableNamePrefix;
    }

    /**
     * 获取下周表名称前缀
     *
     * @return 表名称前缀, 格式 = yyyyMMdd
     */
    public String getNextWeekTableNamePrefix() {
        if (null == _nextWeekTableNamePrefix) {
            _nextWeekTableNamePrefix = DateTimeUtil.getMondayDateStr(_createTime + DateTimeUtil.ONE_WEEK);
        }

        return _nextWeekTableNamePrefix;
    }
}
