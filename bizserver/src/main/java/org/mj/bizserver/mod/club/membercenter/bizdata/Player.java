package org.mj.bizserver.mod.club.membercenter.bizdata;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * ( 牌桌内的 ) 玩家
 */
public final class Player {
    /**
     * 用户 Id
     */
    private int _userId = -1;

    /**
     * 座位索引
     */
    private int _seatIndex = -1;

    /**
     * 用户名称
     */
    private String _userName = null;

    /**
     * 头像
     */
    private String _headImg = null;

    /**
     * 性别
     */
    private int _sex = -1;

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
     * @param val 整数值
     */
    public void setUserId(int val) {
        _userId = val;
    }

    /**
     * 获取座位索引
     *
     * @return 座位索引
     */
    @JSONField(name = "seatIndex")
    public int getSeatIndex() {
        return _seatIndex;
    }

    /**
     * 设置座位索引
     *
     * @param val 整数值
     */
    public void setSeatIndex(int val) {
        _seatIndex = val;
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
     * @param val 字符串值
     */
    public void setUserName(String val) {
        _userName = val;
    }

    /**
     * 获取用户头像
     *
     * @return 用户头像
     */
    @JSONField(name = "headImg")
    public String getHeadImg() {
        return _headImg;
    }

    /**
     * 设置用户头像
     *
     * @param val 字符串值
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
     * @param val 整数值, -1 = 未知, 0 = 女, 1 = 男, 2 = 双性
     */
    public void setSex(int val) {
        _sex = val;
    }

    /**
     * 从 JSON 对象中创建玩家
     *
     * @param jsonObj JSON 对象
     * @return 玩家
     */
    static public Player fromJSON(JSONObject jsonObj) {
        if (null == jsonObj ||
            jsonObj.isEmpty()) {
            return null;
        } else {
            return jsonObj.toJavaObject(Player.class);
        }
    }
}
