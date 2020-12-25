package org.mj.bizserver.mod.record.bizdata;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 玩家
 */
public final class Player {
    /**
     * 用户 Id
     */
    private int _userId;

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
     * 座位索引
     */
    private int _seatIndex;

    /**
     * 当前分数
     */
    private int _currScore;

    /**
     * 总分
     */
    private int _totalScore;

    /**
     * ( 麻将 ) 庄家标志
     */
    private boolean _zhuangFlag;

    /**
     * ( 麻将 ) 自摸
     */
    private boolean _ziMo;

    /**
     * ( 麻将 ) 胡
     */
    private boolean _hu;

    /**
     * ( 麻将 ) 点炮
     */
    private boolean _dianPao;

    /**
     * ( 斗地主 ) 地主
     */
    private boolean _diZhu;

    /**
     * ( 斗地主 ) 农民
     */
    private boolean _nongMin;

    /**
     * ( 斗地主 ) 赢家
     */
    private boolean _winner;

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
     * 设置座位索引
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
     * @param val 座位索引
     */
    public void setSeatIndex(int val) {
        _seatIndex = val;
    }

    /**
     * 获取当前分数
     *
     * @return 当前分数
     */
    @JSONField(name = "currScore")
    public int getCurrScore() {
        return _currScore;
    }

    /**
     * 设置当前分数
     *
     * @param val 当前分数
     */
    public void setCurrScore(int val) {
        _currScore = val;
    }

    /**
     * 获取总分
     *
     * @return 总分
     */
    @JSONField(name = "totalScore")
    public int getTotalScore() {
        return _totalScore;
    }

    /**
     * 设置总分
     *
     * @param val 总分
     */
    public void setTotalScore(int val) {
        _totalScore = val;
    }

    /**
     * 获取 ( 麻将 ) 庄家标志
     *
     * @return 庄家标志
     */
    @JSONField(name = "zhuangFlag")
    public boolean isZhuangFlag() {
        return _zhuangFlag;
    }

    /**
     * 设置 ( 麻将 ) 庄家标志
     *
     * @param val 庄家标志
     */
    public void setZhuangFlag(boolean val) {
        _zhuangFlag = val;
    }

    /**
     * 是否 ( 麻将 ) 自摸
     *
     * @return true = 自摸, false = 不是自摸
     */
    @JSONField(name = "ziMo")
    public boolean isZiMo() {
        return _ziMo;
    }

    /**
     * 设置 ( 麻将 ) 自摸
     *
     * @param val true = 自摸, false = 不是自摸
     */
    public void setZiMo(boolean val) {
        _ziMo = val;
    }

    /**
     * 是否 ( 麻将 ) 胡牌
     *
     * @return true = 胡牌, false = 不是胡牌
     */
    @JSONField(name = "hu")
    public boolean isHu() {
        return _hu;
    }

    /**
     * 设置 ( 麻将 ) 胡牌
     *
     * @param val true = 胡牌, false = 不是胡牌
     */
    public void setHu(boolean val) {
        _hu = val;
    }

    /**
     * 是否 ( 麻将 ) 点炮
     *
     * @return true = 点炮, false = 不是点炮
     */
    @JSONField(name = "dianPao")
    public boolean isDianPao() {
        return _dianPao;
    }

    /**
     * 设置 ( 麻将 ) 点炮
     *
     * @param val true = 点炮, false = 不是点炮
     */
    public void setDianPao(boolean val) {
        _dianPao = val;
    }

    /**
     * 是否 ( 斗地主 ) 地主
     *
     * @return true = 地主, false = 不是地主
     */
    public boolean isDiZhu() {
        return _diZhu;
    }

    /**
     * 设置 ( 斗地主 ) 地主
     *
     * @param val true = 地主, false = 不是地主
     */
    public void setDiZhu(boolean val) {
        _diZhu = val;
    }

    /**
     * 是否 ( 斗地主 ) 农民
     *
     * @return true = 农民, false = 不是农民
     */
    public boolean isNongMin() {
        return _nongMin;
    }

    /**
     * 设置 ( 斗地主 ) 农民
     *
     * @param val true = 农民, false = 不是农民
     */
    public void setNongMin(boolean val) {
        _nongMin = val;
    }

    /**
     * 是否 ( 斗地主 ) 赢家
     *
     * @return true = 赢家, false = 不是赢家
     */
    public boolean isWinner() {
        return _winner;
    }

    /**
     * 设置 ( 斗地主 ) 赢家
     *
     * @param val true = 赢家, false = 不是赢家
     */
    public void setWinner(boolean val) {
        _winner = val;
    }
}
