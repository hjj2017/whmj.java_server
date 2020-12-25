package org.mj.bizserver.mod.userinfo.bizdata;

import org.mj.bizserver.mod.userinfo.dao.UserEntity;

import java.util.Objects;

/**
 * 用户详情
 */
public class UserDetailz {
    /**
     * 用户 Id
     */
    private int _userId;

    /**
     * 用户名称
     */
    private String _userName;

    /**
     * 用户头像
     */
    private String _headImg;

    /**
     * 性别
     */
    private int _sex;

    /**
     * 最后登录 IP
     */
    private String _lastLoginIp;

    /**
     * 最后登录时间
     */
    private long _lastLoginTime;

    /**
     * 房卡
     */
    private int _roomCard;

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
     * 获取用户名称
     *
     * @return 用户名称
     */
    public String getUserName() {
        return Objects.requireNonNullElse(_userName, "");
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
        return Objects.requireNonNullElse(_headImg, "");
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
     * @return 性别, -1 = 未知, 0 = 女, 1 =  男
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
     * 获取最后登陆 IP
     *
     * @return 最后登陆 IP
     */
    public String getLastLoginIp() {
        return Objects.requireNonNullElse(_lastLoginIp, "0.0.0.0");
    }

    /**
     * 设置最后登陆 IP
     *
     * @param val 最后登陆 IP
     */
    public void setLastLoginIp(String val) {
        _lastLoginIp = val;
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
     * @param val 长整数值
     */
    public void setLastLoginTime(long val) {
        _lastLoginTime = val;
    }

    /**
     * 获取房卡数量
     *
     * @return 房卡数量
     */
    public int getRoomCard() {
        return _roomCard;
    }

    /**
     * 设置房卡数量
     *
     * @param val 房卡数量
     */
    public void setRoomCard(int val) {
        _roomCard = val;
    }

    /**
     * 根据用户实体创建用户详情
     *
     * @param entity 用户实体
     * @return 用户详情
     */
    static public UserDetailz fromEntity(UserEntity entity) {
        if (null == entity) {
            return null;
        }

        UserDetailz newDetailz = new UserDetailz();
        newDetailz.setUserId(entity.getUserId());
        newDetailz.setUserName(entity.getUserName());
        newDetailz.setHeadImg(entity.getHeadImg());
        newDetailz.setSex(entity.getSex());
        newDetailz.setLastLoginIp(entity.getLastLoginIp());
        newDetailz.setLastLoginTime(entity.getLastLoginTime());
        newDetailz.setRoomCard(entity.getRoomCard());

        return newDetailz;
    }
}
