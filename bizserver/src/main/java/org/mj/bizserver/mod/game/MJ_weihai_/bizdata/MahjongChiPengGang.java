package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

/**
 * 麻将吃碰杠
 * XXX 注意: 麻将吃、碰、明杠、暗杠、补杠都用一个数据结构表示...
 */
public final class MahjongChiPengGang {
    /**
     * 种类
     */
    private KindDef _kind;

    /**
     * 麻将牌 X
     */
    private final MahjongTileDef _tX;

    /**
     * 第一张牌 ( 记录吃牌时用到 )
     */
    private final MahjongTileDef _t0;

    /**
     * 第二张牌 ( 记录吃牌时用到 )
     */
    private final MahjongTileDef _t1;

    /**
     * 第三张牌 ( 记录吃牌时用到 )
     */
    private final MahjongTileDef _t2;

    /**
     * 麻将牌 X 来自哪个用户 Id, XXX 注意: 可以是自己
     */
    private final int _fromUserId;

    /**
     * 类参数构造器
     *
     * @param kind       种类, 吃碰杠中的哪一种
     * @param tX         吃碰杠的哪一种牌
     * @param t0         第一张牌 ( 记录吃牌时用到 )
     * @param t1         第二张牌 ( 记录吃牌时用到 )
     * @param t2         第三张牌 ( 记录吃牌时用到 )
     * @param fromUserId 来自用户 Id
     */
    MahjongChiPengGang(KindDef kind, MahjongTileDef tX, MahjongTileDef t0, MahjongTileDef t1, MahjongTileDef t2, int fromUserId) {
        _kind = kind;
        _tX = tX;
        _t0 = t0;
        _t1 = t1;
        _t2 = t2;
        _fromUserId = fromUserId;
    }

    /**
     * 获取种类
     *
     * @return 种类定义
     */
    public KindDef getKind() {
        return _kind;
    }

    /**
     * 获取种类整数值
     *
     * @return 整数值
     */
    public int getKindIntVal() {
        return null == _kind ? -1 : _kind.getIntVal();
    }

    /**
     * 获取麻将牌 X,
     * 这张牌意义比较特殊!
     *
     * -- 如果是吃牌, tX 代表吃掉的是哪一张牌;
     * -- 如果是碰牌, tX 代表碰到的是哪一张牌;
     * -- 如果是杠牌, tX 代表杠下的是哪一张牌;
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getTX() {
        return _tX;
    }

    /**
     * 获取麻将牌 X 整数值
     *
     * @return 整数值
     */
    public int getTXIntVal() {
        return null == _tX ? -1 : _tX.getIntVal();
    }

    /**
     * 获取第一张牌 ( 记录吃牌时用到 )
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getT0() {
        return _t0;
    }

    /**
     * 获取第一张牌整数值
     *
     * @return 整数值
     */
    public int getT0IntVal() {
        return null == _t0 ? -1 : _t0.getIntVal();
    }

    /**
     * 获取第二张牌 ( 记录吃牌时用到 )
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getT1() {
        return _t1;
    }

    /**
     * 获取第二张牌整数值
     *
     * @return 整数值
     */
    public int getT1IntVal() {
        return null == _t1 ? -1 : _t1.getIntVal();
    }

    /**
     * 获取第三张牌 ( 记录吃牌时用到 )
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getT2() {
        return _t2;
    }

    /**
     * 获取第三张牌整数值
     *
     * @return 整数值
     */
    public int getT2IntVal() {
        return null == _t2 ? -1 : _t2.getIntVal();
    }

    /**
     * 获取来自用户 Id
     *
     * @return 用户 Id
     */
    public int getFromUserId() {
        return _fromUserId;
    }

    /**
     * 升级碰牌为补杠
     */
    public void upgradePengToBuGang() {
        if (KindDef.PENG == _kind) {
            _kind = KindDef.BU_GANG;
        }
    }

    /**
     * 种类定义
     */
    public enum KindDef {
        /**
         * 吃
         */
        CHI(1, "chi"),

        /**
         * 碰
         */
        PENG(2, "peng"),

        /**
         * 明杠
         */
        MING_GANG(3, "mingGang"),

        /**
         * 暗杠
         */
        AN_GANG(4, "anGang"),

        /**
         * 补杠
         */
        BU_GANG(5, "buGang"),
        ;

        /**
         * 整数值
         */
        private final int _intVal;

        /**
         * 字符串值
         */
        private final String _strVal;

        /**
         * 枚举参数构造器
         *
         * @param intVal 整数值
         * @param strVal 字符串值
         */
        KindDef(int intVal, String strVal) {
            _intVal = intVal;
            _strVal = strVal;
        }

        /**
         * 获取整数值
         *
         * @return 整数值
         */
        public int getIntVal() {
            return _intVal;
        }

        /**
         * 获取字符串值
         *
         * @return 字符串值
         */
        public String getStrVal() {
            return _strVal;
        }
    }
}
