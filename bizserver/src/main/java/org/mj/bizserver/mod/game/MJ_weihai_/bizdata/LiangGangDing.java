package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

/**
 * 亮杠腚, 也就杠牌之后可以抓到的牌.
 * 因为亮杠腚只显示两张牌,
 * 所以在这里通过 t0 和 t1 来模拟一个队列...
 */
class LiangGangDing {
    /**
     * 第一张牌
     */
    private MahjongTileDef _t0 = null;

    /**
     * 第二张牌
     */
    private MahjongTileDef _t1 = null;

    /**
     * 类默认构造器
     */
    LiangGangDing() {
    }

    /**
     * 获取第一张牌
     *
     * @return 第一张牌
     */
    MahjongTileDef getT0() {
        return _t0;
    }

    /**
     * 获取第二张牌
     *
     * @return 第二张牌
     */
    MahjongTileDef getT1() {
        return _t1;
    }

    /**
     * 将麻将牌加入队列
     *
     * @param val 对象值
     */
    void offer(MahjongTileDef val) {
        if (null == val) {
            return;
        }

        if (null != _t1) {
            _t0 = _t1;
        }

        _t1 = val;
    }

    /**
     * 从队列中取出麻将牌
     *
     * @return 麻将牌定义
     */
    MahjongTileDef poll() {
        if (null == _t0) {
            _t0 = _t1;
            _t1 = null;
        }

        MahjongTileDef head = _t0;
        _t0 = _t1;
        _t1 = null;

        return head;
    }

    /**
     * 获取数量
     *
     * @return 数量
     */
    int count() {
        int count = 0;

        if (null != _t0) {
            ++count;
        }

        if (null != _t1) {
            ++count;
        }

        return count;
    }
}
