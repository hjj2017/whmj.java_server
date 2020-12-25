package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 吃碰杠胡对话
 */
class ChiPengGangHuDialog {
    /**
     * 操作 - 吃
     */
    static final int OP_CHI = 1;

    /**
     * 操作 - 碰
     */
    static final int OP_PENG = (1 << 1);

    /**
     * 操作 - 明杠
     */
    static final int OP_MING_GANG = (1 << 2);

    /**
     * 操作 - 暗杠
     */
    static final int OP_AN_GANG = (1 << 3);

    /**
     * 操作 - 补杠
     */
    static final int OP_BU_GANG = (1 << 4);

    /**
     * 操作 - 胡
     */
    static final int OP_HU = (1 << 5);

    /**
     * 操作 - 自摸
     */
    static final int OP_ZI_MO = (1 << 6);

    /**
     * 操作 - 亮风
     */
    static final int OP_LIANG_FENG = (1 << 7);

    /**
     * 操作 - 补风
     */
    static final int OP_BU_FENG = (1 << 8);

    /**
     * 用户 Id 和操作字典
     */
    Map<Integer, Integer> _userIdCanOpMap;

    /**
     * 用户 Id 是否可以吃牌
     *
     * @param userId 用户 Id
     * @return true = 可以吃牌, false = 不能吃牌
     */
    public boolean isUserIdCanChi(int userId) {
        return isUserIdCanOp(
            userId, OP_CHI
        );
    }

    /**
     * 用户 Id 是否可以碰牌
     *
     * @param userId 用户 Id
     * @return true = 可以碰, false = 不能碰
     */
    public boolean isUserIdCanPeng(int userId) {
        return isUserIdCanOp(
            userId, OP_PENG
        );
    }

    /**
     * 用户 Id 是否可以明杠
     *
     * @param userId 用户 Id
     * @return true = 可以明杠, false = 不能明杠
     */
    public boolean isUserIdCanMingGang(int userId) {
        return isUserIdCanOp(
            userId,
            OP_MING_GANG
        );
    }

    /**
     * 用户 Id 是否可以暗杠
     *
     * @param userId 用户 Id
     * @return true = 可以暗杠, false = 不能暗杠
     */
    public boolean isUserIdCanAnGang(int userId) {
        return isUserIdCanOp(
            userId,
            OP_AN_GANG
        );
    }

    /**
     * 用户 Id 是否可以补杠
     *
     * @param userId 用户 Id
     * @return true = 可以暗杠, false = 不能补杠
     */
    public boolean isUserIdCanBuGang(int userId) {
        return isUserIdCanOp(
            userId,
            OP_BU_GANG
        );
    }

    /**
     * 用户 Id 是否可以胡牌
     *
     * @param userId 用户 Id
     * @return true = 可以胡牌, false = 不能胡牌
     */
    public boolean isUserIdCanHu(int userId) {
        return isUserIdCanOp(
            userId,
            OP_HU
        );
    }

    /**
     * 用户 Id 是否可以自摸
     *
     * @param userId 用户 Id
     * @return true = 可以自摸, false = 不能自摸
     */
    public boolean isUserIdCanZiMo(int userId) {
        return isUserIdCanOp(
            userId,
            OP_ZI_MO
        );
    }

    /**
     * 用户是否可以亮风
     *
     * @param userId 用户 Id
     * @return true = 可以亮风, false = 不能亮风
     */
    public boolean isUserIdCanLiangFeng(int userId) {
        return isUserIdCanOp(
            userId,
            OP_LIANG_FENG
        );
    }

    /**
     * 用户是否可以补风
     *
     * @param userId 用户 Id
     * @return true = 可以亮风, false = 不能亮风
     */
    public boolean isUserIdCanBuFeng(int userId) {
        return isUserIdCanOp(
            userId,
            OP_BU_FENG
        );
    }

    /**
     * 设置用户可以执行操作
     *
     * @param userId 用户 Id
     * @param newOp  新操作
     */
    void setUserIdCanOp(int userId, int newOp) {
        if (userId <= 0 ||
            newOp <= 0) {
            return;
        }

        if (null == _userIdCanOpMap) {
            _userIdCanOpMap = new HashMap<>();
        }

        // 获取已有操作
        Integer existOp = _userIdCanOpMap.getOrDefault(userId, 0);
        // 叠加操作
        _userIdCanOpMap.put(userId, existOp | newOp);
    }

    /**
     * 用户 Id 是否可以执行指定操作
     *
     * @param userId 用户 Id
     * @param op     操作
     * @return true = 可以执行, false = 不能执行
     */
    private boolean isUserIdCanOp(int userId, int op) {
        if (null == _userIdCanOpMap ||
            _userIdCanOpMap.isEmpty()) {
            return false;
        }

        // 获取已有操作
        Integer op_exist = _userIdCanOpMap.get(userId);

        if (null == op_exist) {
            return false;
        } else {
            return 0 != (op_exist & op);
        }
    }

    /**
     * 当前用户 Id 是否是活动用户
     * <p>
     * XXX 注意: 这里和 Round 的逻辑不一样, Round 用的是 _actSeatIndex.
     * 这里用的是 _actPlayerId...
     * 正常牌局的行牌顺序是逆时针转,
     * 但是在提示吃碰杠胡的逻辑时, 操作优先级是: 胡 > 杠 > 碰 > 吃!
     * 无法保证可以操作的玩家正好符合逆时针顺序
     *
     * @return 用户 Id
     */
    public boolean isCurrActUserId(int userId) {
        return isUserIdCanOp(
            userId, OP_CHI | OP_PENG | OP_MING_GANG | OP_AN_GANG | OP_BU_GANG | OP_HU | OP_ZI_MO | OP_LIANG_FENG | OP_BU_FENG
        );
    }

    /**
     * 擦除用户 Id
     *
     * @param userId 用户 Id
     */
    public void eraseUserId(int userId) {
        if (null != _userIdCanOpMap) {
            _userIdCanOpMap.remove(userId);
        }
    }

    /**
     * 对话是否结束
     *
     * @return true = 已结束, false = 没有结束
     */
    public boolean isFinished() {
        return null == _userIdCanOpMap || _userIdCanOpMap.isEmpty();
    }

    /**
     * 获取可以操作的用户 Id 集合
     *
     * @return 用户 Id 集合
     */
    public Set<Integer> getUserIdSetCanOp() {
        if (null == _userIdCanOpMap ||
            _userIdCanOpMap.isEmpty()) {
            return null;
        } else {
            return _userIdCanOpMap.keySet();
        }
    }

    /**
     * 是否是唯一的用户 Id
     *
     * @param userId 用户 Id
     * @return true = 是唯一, false = 不是唯一
     */
    boolean isJustOneUserId(int userId) {
        if (null == _userIdCanOpMap ||
            _userIdCanOpMap.size() != 1) {
            return false;
        } else {
            return _userIdCanOpMap.getOrDefault(userId, 0) != 0;
        }
    }
}
