package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

/**
 * 状态表, 隶属于玩家, 结构如下:
 * <pre>
 * Player
 *   |
 *   +-- StateTable ( 当前类 )
 *   |
 *   +-- SettlementResult
 * </pre>
 *
 * @see SettlementResult
 */
public final class StateTable {
    /**
     * 准备
     */
    private boolean _prepare = false;

    /**
     * 庄家
     */
    private boolean _zhuangJia;

    /**
     * 漂几, 默认 = -1, 未选择飘
     */
    private int _piaoX = -1;

    /**
     * 胡 ( 由于别人点炮 )
     */
    private boolean _hu;

    /**
     * 胡的是哪一张麻将牌
     */
    private MahjongTileDef _mahjongHu;

    /**
     * 点炮
     */
    private boolean _dianPao;

    /**
     * 自摸
     */
    private boolean _ziMo;

    /**
     * 麻将自摸
     */
    private MahjongTileDef _mahjongZiMo;

    /**
     * 刚好有杠的数量, 用作杠上开花、杠上点炮
     */
    private int _justGangNum = 0;

    /**
     * 是否准备
     *
     * @return true = 已准备, false = 未准备
     */
    public boolean isPrepare() {
        return _prepare;
    }

    /**
     * 设置准备
     *
     * @param val 准备标志
     */
    public void setPrepare(boolean val) {
        _prepare = val;
    }

    /**
     * 是否为庄家
     *
     * @return true = 是, false = 不是
     */
    public boolean isZhuangJia() {
        return _zhuangJia;
    }

    /**
     * 设置庄家
     *
     * @param val 布尔值
     */
    public void setZhuangJia(boolean val) {
        _zhuangJia = val;
    }

    /**
     * 获取漂几
     *
     * @return 漂几, 0 = 不飘, 1 = 飘_1, 2 = 飘_2, 3 = 飘_3, 4 = 飘_4
     */
    public int getPiaoX() {
        return _piaoX;
    }

    /**
     * 设置漂几
     *
     * @param val 整数值
     */
    public void setPiaoX(int val) {
        _piaoX = val;
    }

    /**
     * 是否胡牌 ( 由于他人点炮 )
     *
     * @return true = 是胡牌, false = 没有胡牌
     */
    public boolean isHu() {
        return _hu;
    }

    /**
     * 设置胡牌
     *
     * @param val 布尔值
     */
    public void setHu(boolean val) {
        _hu = val;
    }

    /**
     * 获取胡的是哪一张麻将牌 ( 也就是他人用的哪一张牌点的炮 )
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getMahjongHu() {
        return _mahjongHu;
    }

    /**
     * 获取胡的是哪一张麻将牌整数值
     *
     * @return 整数值
     */
    public int getMahjongHuIntVal() {
        return (null == _mahjongHu) ? -1 : _mahjongHu.getIntVal();
    }

    /**
     * 设置麻将胡牌
     *
     * @param val 麻将牌定义
     */
    public void setMahjongHu(MahjongTileDef val) {
        _mahjongHu = val;
    }

    /**
     * 是否点炮
     *
     * @return true = 是点炮, false = 不是点炮
     */
    public boolean isDianPao() {
        return _dianPao;
    }

    /**
     * 设置点炮
     *
     * @param val 布尔值
     */
    public void setDianPao(boolean val) {
        _dianPao = val;
    }

    /**
     * 是否自摸
     *
     * @return true = 是自摸, false = 不是自摸
     */
    public boolean isZiMo() {
        return _ziMo;
    }

    /**
     * 设置自摸
     *
     * @param val 布尔值
     */
    public void setZiMo(boolean val) {
        _ziMo = val;
    }

    /**
     * 获取自摸麻将牌
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getMahjongZiMo() {
        return _mahjongZiMo;
    }

    /**
     * 获取自摸麻将牌整数值
     *
     * @return 自摸麻将牌整数值
     */
    public int getMahjongZiMoIntVal() {
        return (null == _mahjongZiMo) ? -1 : _mahjongZiMo.getIntVal();
    }

    /**
     * 设置自摸麻将牌
     *
     * @param val 麻将牌定义
     */
    public void setMahjongZiMo(MahjongTileDef val) {
        _mahjongZiMo = val;
    }

    /**
     * 获取刚好有杠的数量
     *
     * @return 刚好有杠的数量
     */
    public int getJustGangNum() {
        return _justGangNum;
    }

    /**
     * 刚好有杠的数量 +1
     */
    public void increaseJustGangNum() {
        ++_justGangNum;
    }

    /**
     * 刚好有杠的数量清零
     */
    public void resetJustGangNum() {
        _justGangNum = 0;
    }
}
