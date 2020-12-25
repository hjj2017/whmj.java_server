package org.mj.bizserver.mod.record.dao;

import org.mj.comm.util.DateTimeUtil;

import java.util.Objects;

/**
 * 牌局日志实体
 */
public class RoundLogEntity {
    /**
     * 房间 UUId
     */
    private String _roomUUId;

    /**
     * 当前牌局索引
     */
    private Integer _roundIndex;

    /**
     * 创建时间
     */
    private Long _createTime;

    /**
     * 所有玩家
     */
    private String _allPlayer;

    /**
     * 所有当前分数
     */
    private String _allCurrScore;

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
     * 回放票根
     */
    private String _playbackStub;

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
     * 获取牌局索引
     *
     * @return 牌局索引
     */
    public Integer getRoundIndex() {
        return _roundIndex;
    }

    /**
     * 设置牌局索引
     *
     * @param val 牌局索引
     */
    public void setRoundIndex(Integer val) {
        _roundIndex = val;
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
     * 获取所有当前分数 ( JSON 字符串 )
     *
     * @return 所有当前分数
     */
    public String getAllCurrScore() {
        return _allCurrScore;
    }

    /**
     * 设置所有当前分数 ( JSON 字符串 )
     *
     * @param val 所有当前分数
     */
    public void setAllCurrScore(String val) {
        _allCurrScore = val;
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
     * 获取回放票根
     *
     * @return 回放票根
     */
    public String getPlaybackStub() {
        return Objects.requireNonNullElse(_playbackStub, "");
    }

    /**
     * 设置回放票根
     *
     * @param val 回放票根
     */
    public void setPlaybackStub(String val) {
        _playbackStub = val;
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
