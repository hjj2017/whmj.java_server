package org.mj.bizserver.mod.userinfo.dao;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 用户实体
 */
public class UserEntity {
    /**
     * 用户 Id
     */
    private int _userId;

    /**
     * 手机号
     */
    private String _phoneNumber;

    /**
     * 用户名称
     */
    private String _userName;

    /**
     * 头像
     */
    private String _headImg;

    /**
     * 性别, 0 = 女, 1 = 男
     */
    private int _sex;

    /**
     * 房卡
     */
    private int _roomCard;

    /**
     * 创建时间
     */
    private long _createTime;

    /**
     * 客户端版本号
     */
    private String _clientVer;

    /**
     * 最后登录时间
     */
    private long _lastLoginTime;

    /**
     * 最后登录 Ip
     */
    private String _lastLoginIp;

    /**
     * 状态
     */
    private int _state;

    /**
     * 获取用户 Id
     *
     * @return 用户 Id
     */
    @JSONField(name = "userId")
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
     * 获取手机号
     *
     * @return 手机号
     */
    @JSONField(name = "phoneNumber")
    public String getPhoneNumber() {
        return _phoneNumber;
    }

    /**
     * 设置手机号
     *
     * @param val 字符串值
     */
    public void setPhoneNumber(String val) {
        _phoneNumber = val;
    }

    /**
     * 获取用户名称
     *
     * @return 用户名称
     */
    @JSONField(name = "userName")
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
    @JSONField(name = "headImg")
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
     * @return 性别, -1 = 未知, 0 = 女, 1 = 男, 2 = 双性
     */
    @JSONField(name = "sex")
    public int getSex() {
        return _sex;
    }

    /**
     * 设置性别
     *
     * @param val 性别, -1 = 未知, 0 = 女, 1 = 男, 2 = 双性
     */
    public void setSex(int val) {
        _sex = val;
    }

    /**
     * 获取房卡数量
     *
     * @return 房卡数量
     */
    @JSONField(name = "roomCard")
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
     * 获取创建时间
     *
     * @return 创建时间
     */
    @JSONField(name = "createTime")
    public long getCreateTime() {
        return _createTime;
    }

    /**
     * 设置创建时间
     *
     * @param val 创建时间
     */
    public void setCreateTime(long val) {
        _createTime = val;
    }

    /**
     * 获取客户端版本号
     *
     * @return 客户端版本号
     */
    @JSONField(name = "clientVer")
    public String getClientVer() {
        return _clientVer;
    }

    /**
     * 设置客户端版本号
     *
     * @param val 客户端版本号
     */
    public void setClientVer(String val) {
        _clientVer = val;
    }

    /**
     * 获取最后登陆时间
     *
     * @return 最后登陆时间
     */
    @JSONField(name = "lastLoginTime")
    public long getLastLoginTime() {
        return _lastLoginTime;
    }

    /**
     * 设置最后登陆时间
     *
     * @param val 最后登陆时间
     */
    public void setLastLoginTime(long val) {
        _lastLoginTime = val;
    }

    /**
     * 获取最后登陆 IP
     *
     * @return 最后登陆 IP
     */
    @JSONField(name = "lastLoginIp")
    public String getLastLoginIp() {
        return _lastLoginIp;
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
     * 获取状态
     *
     * @return 状态
     */
    @JSONField(name = "state")
    public int getState() {
        return _state;
    }

    /**
     * 设置状态
     *
     * @param val 状态
     */
    public void setState(int val) {
        _state = val;
    }

    /**
     * 根据 JSON 字符串创建实体
     *
     * @param jsonStr JSON 字符串
     * @return 用户实体
     */
    static public UserEntity fromJSONStr(String jsonStr) {
        if (null == jsonStr ||
            !jsonStr.startsWith("{") ||
            !jsonStr.endsWith("}")) {
            return null;
        }

        return JSONObject.parseObject(jsonStr, UserEntity.class);
    }
}
