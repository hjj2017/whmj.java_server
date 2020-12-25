package org.mj.bizserver.mod.userlogin.bizdata;

import java.util.Objects;

/**
 * 登陆票据
 */
public class LoginResult {
    /**
     * 用户 Id
     */
    private int _userId;

    /**
     * 用户名称
     */
    private String _userName;

    /**
     * 票据
     */
    private String _ticket;

    /**
     * 数字证书字符串
     */
    private String _ukeyStr;

    /**
     * 数字证书过期时间
     */
    private long _ukeyExpireAt;

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
     * 获取票据
     *
     * @return 票据
     */
    public String getTicket() {
        return Objects.requireNonNullElse(_ticket, "");
    }

    /**
     * 设置票据
     *
     * @param val 票据
     */
    public void setTicket(String val) {
        _ticket = val;
    }

    /**
     * 获取数字证书字符串
     *
     * @return 数字证书字符串
     */
    public String getUkeyStr() {
        return _ukeyStr;
    }

    /**
     * 设置数字证书字符串
     *
     * @param val 字符串值
     */
    public void setUkeyStr(String val) {
        _ukeyStr = val;
    }

    /**
     * 获取数字证书过期时间
     *
     * @return 数字证书过期时间
     */
    public long getUkeyExpireAt() {
        return _ukeyExpireAt;
    }

    /**
     * 设置数字证书过期时间
     *
     * @param val 整数值
     */
    public void setUkeyExpireAt(long val) {
        _ukeyExpireAt = val;
    }
}
