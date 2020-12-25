package org.mj.bizserver.mod.game.MJ_weihai_.dao;

import java.util.Objects;

/**
 * 消耗房卡数量配置实体
 */
public class CostRoomCardConfEntity {
    /**
     * 虚设 Id
     */
    private int _dummyId;

    /**
     * 游戏类型 0
     */
    private int _gameType0;

    /**
     * 游戏类型 1
     */
    private int _gameType1;

    /**
     * 最大玩家数量
     */
    private int _maxPlayer;

    /**
     * 最大局数
     */
    private int _maxRound;

    /**
     * 最大圈数
     */
    private int _maxCircle;

    /**
     * 亲友圈支付所需房卡数量
     */
    private int _paymentWayClub;

    /**
     * 房主支付所需房卡数量
     */
    private int _paymentWayRoomOwner;

    /**
     * AA 支付所需房卡数量
     */
    private int _paymentWayAA;

    /**
     * 获取虚设 Id
     *
     * @return 虚设 Id
     */
    public int getDummyId() {
        return _dummyId;
    }

    /**
     * 设置虚设 Id
     *
     * @param val 整数值
     */
    public void setDummyId(int val) {
        _dummyId = val;
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
     * 获取最大玩家数量
     *
     * @return 最大玩家数量
     */
    public int getMaxPlayer() {
        return _maxPlayer;
    }

    /**
     * 设置最大玩家数量
     *
     * @param val 最大玩家数量
     */
    public void setMaxPlayer(int val) {
        _maxPlayer = val;
    }

    /**
     * 获取最大局数
     *
     * @return 最大局数
     */
    public int getMaxRound() {
        return _maxRound;
    }

    /**
     * 设置最大局数
     *
     * @param val 最大局数
     */
    public void setMaxRound(int val) {
        _maxRound = val;
    }

    /**
     * 获取最大圈数
     *
     * @return 最大圈数
     */
    public int getMaxCircle() {
        return _maxCircle;
    }

    /**
     * 设置最大圈数
     *
     * @param val 整数值
     */
    public void setMaxCircle(int val) {
        _maxCircle = val;
    }

    /**
     * 获取亲友圈支付方式所需房卡数量
     *
     * @return 所需房卡数量
     */
    public int getPaymentWayClub() {
        return _paymentWayClub;
    }

    /**
     * 设置亲友圈支付方式所需房卡数量
     *
     * @param val 所需房卡数量
     */
    public void setPaymentWayClub(int val) {
        _paymentWayClub = val;
    }

    /**
     * 获取房主支付方式所需房卡数量
     *
     * @return 所需房卡数量
     */
    public int getPaymentWayRoomOwner() {
        return Objects.requireNonNullElse(_paymentWayRoomOwner, 1);
    }

    /**
     * 设置房主支付方式所需房卡数量
     *
     * @param val 所需房卡数量
     */
    public void setPaymentWayRoomOwner(int val) {
        _paymentWayRoomOwner = val;
    }

    /**
     * 获取 AA 支付方式所需房卡数量
     *
     * @return 所需房卡数量
     */
    public int getPaymentWayAA() {
        return _paymentWayAA;
    }

    /**
     * 设置 AA 支付方式所需房卡数量
     *
     * @param val 所需房卡数量
     */
    public void setPaymentWayAA(int val) {
        _paymentWayAA = val;
    }
}
