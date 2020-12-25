package org.mj.bizserver.mod.club.adminctrl.dao;

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
     * 创建人 ( 用户 ) 名称
     */
    private String _creatorName;

    /**
     * 创建人头像
     */
    private String _creatorHeadImg;

    /**
     * 创建人性别
     */
    private Integer _creatorSex;

    /**
     * 创建时间
     */
    private Long _createTime;

    /**
     * 当前状态
     */
    private Integer _currState;

    /**
     * 获取亲友圈 Id
     *
     * @return 亲友圈 Id
     */
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
     * 获取创建人 ( 用户 ) 名称
     *
     * @return 创建人 ( 用户 ) 名称
     */
    public String getCreatorName() {
        return Objects.requireNonNullElse(_creatorName, "");
    }

    /**
     * 设置创建人 ( 用户 ) 名称
     *
     * @param val 创建人 ( 用户 ) 名称
     */
    public void setCreatorName(String val) {
        _creatorName = val;
    }

    /**
     * 获取创建人头像
     *
     * @return 创建人头像
     */
    public String getCreatorHeadImg() {
        return Objects.requireNonNullElse(_creatorHeadImg, "");
    }

    /**
     * 设置创建人头像
     *
     * @param val 创建人头像
     */
    public void setCreatorHeadImg(String val) {
        _creatorHeadImg = val;
    }

    /**
     * 获取创建人性别
     *
     * @return 性别, -1 = 未知, 0 = 女, 1 = 男
     */
    public Integer getCreatorSex() {
        return Objects.requireNonNullElse(_creatorSex, -1);
    }

    /**
     * 设置创建人性别
     *
     * @param val 性别, -1 = 未知, 0 = 女, 1 = 男
     */
    public void setCreatorSex(Integer val) {
        _creatorSex = val;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
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
     * 获取当前状态
     *
     * @return 当前状态
     */
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
