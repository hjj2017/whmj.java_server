package org.mj.bizserver.mod.club.membercenter.dao;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Objects;

/**
 * 亲友圈成员
 */
public final class ClubMemberEntity {
    /**
     * 用户 Id
     */
    private Integer _userId;

    /**
     * 亲友圈 Id
     */
    private Integer _clubId;

    /**
     * 当前角色
     */
    private Integer _role;

    /**
     * 加入时间
     */
    private Long _joinTime;

    /**
     * 当前状态
     */
    private Integer _currState;

    /**
     * 获取用户 Id
     *
     * @return 用户 Id
     */
    @JSONField(name = "userId")
    public Integer getUserId() {
        return Objects.requireNonNullElse(_userId, 0);
    }

    /**
     * 设置用户 Id
     *
     * @param val 用户 Id
     */
    public void setUserId(Integer val) {
        _userId = val;
    }

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
     * 获取角色
     *
     * @return 角色
     */
    @JSONField(name = "role")
    public Integer getRole() {
        return Objects.requireNonNullElse(_role, 0);
    }

    /**
     * 设置角色
     *
     * @param val 角色
     */
    public void setRole(Integer val) {
        _role = val;
    }

    /**
     * 获取加入时间
     *
     * @return 加入时间
     */
    @JSONField(name = "joinTime")
    public Long getJoinTime() {
        return Objects.requireNonNullElse(_joinTime, 0L);
    }

    /**
     * 设置加入时间
     *
     * @param val 加入时间
     */
    public void setJoinTime(Long val) {
        _joinTime = val;
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态, -256 = 永久踢出, -1 = 主动退出, 0 = 申请加入, 1 = 已加入
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
}
