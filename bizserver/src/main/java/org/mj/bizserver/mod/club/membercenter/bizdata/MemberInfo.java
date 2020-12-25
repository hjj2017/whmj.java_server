package org.mj.bizserver.mod.club.membercenter.bizdata;

import org.mj.bizserver.mod.club.membercenter.dao.ClubMemberEntity;

/**
 * 亲友圈成员信息
 */
public class MemberInfo {
    /**
     * 用户 Id
     */
    private int _userId;

    /**
     * 角色
     */
    private RoleDef _role;

    /**
     * 用户名称
     */
    private String _userName;

    /**
     * 头像
     */
    private String _headImg;

    /**
     * 性别
     */
    private int _sex;

    /**
     * 加入时间
     */
    private long _joinTime;

    /**
     * 最后登录时间
     */
    private long _lastLoginTime;

    /**
     * 当前状态
     */
    private MemberStateEnum _currState;

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
     * @param val 用户 Id
     */
    public void setUserId(int val) {
        _userId = val;
    }

    /**
     * 获取角色
     *
     * @return 角色
     */
    public RoleDef getRole() {
        return _role;
    }

    /**
     * 获取角色整数值
     *
     * @return 角色整数值
     */
    public int getRoleIntVal() {
        if (null == _role) {
            return -1;
        } else {
            return _role.getIntVal();
        }
    }

    /**
     * 设置角色
     *
     * @param val 角色
     */
    public void setRole(RoleDef val) {
        _role = val;
    }

    /**
     * 获取用户名称
     *
     * @return 用户名称
     */
    public String getUserName() {
        return _userName;
    }

    /**
     * 设置用户名称
     *
     * @param val 用户名称
     */
    public void setUserName(String val) {
        _userName = val;
    }

    /**
     * 获取头像
     *
     * @return 头像
     */
    public String getHeadImg() {
        return _headImg;
    }

    /**
     * 设置头像
     *
     * @param val 头像
     */
    public void setHeadImg(String val) {
        _headImg = val;
    }

    /**
     * 获取性别
     *
     * @return 性别, -1 = 未知, 0 = 女, 1 = 男
     */
    public int getSex() {
        return _sex;
    }

    /**
     * 设置性别
     *
     * @param val 性别, -1 = 未知, 0 = 女, 1 = 男
     */
    public void setSex(int val) {
        _sex = val;
    }

    /**
     * 获取加入时间
     *
     * @return 加入时间
     */
    public long getJoinTime() {
        return _joinTime;
    }

    /**
     * 设置加入时间
     *
     * @param val 加入时间
     */
    public void setJoinTime(long val) {
        _joinTime = val;
    }

    /**
     * 获取最后登录时间
     *
     * @return 最后登录时间
     */
    public long getLastLoginTime() {
        return _lastLoginTime;
    }

    /**
     * 设置最后登录时间
     *
     * @param val 最后登录时间
     */
    public void setLastLoginTime(long val) {
        _lastLoginTime = val;
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public MemberStateEnum getCurrState() {
        return _currState;
    }

    /**
     * 获取当前状态整数值
     *
     * @return 当前状态整数值
     */
    public int getCurrStateIntVal() {
        if (null == _currState) {
            return MemberStateEnum.REJECT.getIntVal();
        } else {
            return _currState.getIntVal();
        }
    }

    /**
     * 设置当前状态
     *
     * @param val 当前状态
     */
    public void setCurrState(MemberStateEnum val) {
        _currState = val;
    }

    /**
     * 从实体中创建业务对象
     *
     * @param entity 实体
     * @return 业务对象
     */
    static public MemberInfo fromEntity(ClubMemberEntity entity) {
        if (null == entity) {
            return null;
        }

        final MemberInfo bizObj = new MemberInfo();
        bizObj.setUserId(entity.getUserId());
        bizObj.setRole(RoleDef.valueOf(entity.getRole()));
        bizObj.setJoinTime(entity.getJoinTime());
        bizObj.setCurrState(MemberStateEnum.valueOf(entity.getCurrState()));

        return bizObj;
    }
}
