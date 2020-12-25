package org.mj.bizserver.mod.stat.dao;

import org.mj.comm.util.DateTimeUtil;

/**
 * 用户游戏日志实体
 */
public class UserGameLogEntity {
    /**
     * 用户 Id
     */
    private int _userId;

    /**
     * 亲友圈 Id
     */
    private int _clubId;

    /**
     * 房间 Id
     */
    private int _roomId;

    /**
     * 房间 UUId
     */
    private String _roomUUId;

    /**
     * 游戏类型 0
     */
    private int _gameType0;

    /**
     * 游戏类型 1
     */
    private int _gameType1;

    /**
     * 创建时间
     */
    private long _createTime;

    /**
     * 总分
     */
    private int _totalScore;

    /**
     * 是否为大赢家
     */
    private int _isWinner;

    /**
     * 本周表名称前缀
     */
    private String _thisWeekTableNamePrefix;

    /**
     * 下周表名称前缀
     */
    private String _nextWeekTableNamePrefix;

    /**
     * 获取用户 Id
     *
     * @return 用户 Id
     */
    public int getUserId() {
        return _userId;
    }

    /**
     * 设置用户 Id
     *
     * @param val 整数值
     */
    public void setUserId(int val) {
        _userId = val;
    }

    /**
     * 获取亲友圈 Id
     *
     * @return 亲友圈 Id
     */
    public int getClubId() {
        return _clubId;
    }

    /**
     * 设置亲友圈 Id
     *
     * @param val 整数值
     */
    public void setClubId(int val) {
        _clubId = val;
    }

    /**
     * 获取房间 Id
     *
     * @return 房间 Id
     */
    public int getRoomId() {
        return _roomId;
    }

    /**
     * 设置房间 Id
     *
     * @param val 整数值
     */
    public void setRoomId(int val) {
        _roomId = val;
    }

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
     * @param val 字符串值
     */
    public void setRoomUUId(String val) {
        _roomUUId = val;
    }

    /**
     * 获取游戏类型 0
     *
     * @return 游戏类型 0
     */
    public int getGameType0() {
        return _gameType0;
    }

    /**
     * 设置游戏类型 0
     *
     * @param val 整数值
     */
    public void setGameType0(int val) {
        _gameType0 = val;
    }

    /**
     * 获取游戏类型 1
     *
     * @return 游戏类型 1
     */
    public int getGameType1() {
        return _gameType1;
    }

    /**
     * 设置游戏类型 1
     *
     * @param val 整数值
     */
    public void setGameType1(int val) {
        _gameType1 = val;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public long getCreateTime() {
        return _createTime;
    }

    /**
     * 设置创建时间
     *
     * @param val 整数值
     */
    public void setCreateTime(long val) {
        _createTime = val;
    }

    /**
     * 获取总分
     *
     * @return 总分
     */
    public int getTotalScore() {
        return _totalScore;
    }

    /**
     * 设置总分
     *
     * @param val 整数值
     */
    public void setTotalScore(int val) {
        _totalScore = val;
    }

    /**
     * 获取是否为大赢家
     *
     * @return 1 = 大赢家, 0 = 不是大赢家
     */
    public int getIsWinner() {
        return _isWinner;
    }

    /**
     * 设置是否为大赢家
     *
     * @param val 1 = 大赢家, 0 = 不是大赢家
     */
    public void setIsWinner(int val) {
        _isWinner = val;
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
