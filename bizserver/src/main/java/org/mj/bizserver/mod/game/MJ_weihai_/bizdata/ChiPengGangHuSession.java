package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * 吃碰杠胡会议, 与 ChiPengGangHuDialog 是一个一对多的关系:
 *
 * <pre>
 * ChiPengGangHuSession
 *     +-- ChiPengGangHuDialog
 *     +-- ChiPengGangHuDialog
 *     +-- ...
 * </pre>
 * <p>
 * XXX 注意: ChiPengGangHuDialog 只是维系与用户的对话,
 * 判断某个用户是否可以操作?
 * <p>
 * 而具体的可以吃碰杠胡的是哪一张麻将牌,
 * 还是会放在 ChiPengGangHuSession 中...
 *
 * @see ChiPengGangHuDialog
 */
public final class ChiPengGangHuSession {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(ChiPengGangHuSession.class);

    /**
     * 来自他人的麻将牌,
     * 这张牌可以且只能用作吃、碰、明杠、胡 ( 别人点炮我胡牌 )
     */
    private final MahjongTileDef _fromOtherzT;

    /**
     * 来自用户 Id,
     * 也就是 "来自他人的麻将牌 ( _fromOtherzT ) " 到底是从谁那里来的
     */
    private final int _fromUserId;

    /**
     * 可以吃牌的用户 Id
     */
    private int _userIdCanChi;

    /**
     * 吃牌选择题
     */
    private ChiChoiceQuestion _chiChoiceQuestion = null;

    /**
     * 可以碰牌的用户 Id
     */
    private int _userIdCanPeng;

    /**
     * 可以明杠的用户 Id
     */
    private int _userIdCanMingGang;

    /**
     * 可以暗杠的用户 Id
     */
    private int _userIdCanAnGang;

    /**
     * 可以补杠的用户 Id
     */
    private int _userIdCanBuGang;

    /**
     * 可以自摸 ( 胡牌 ) 的用户 Id
     */
    private int _userIdCanZiMo;

    /**
     * 可以胡牌的用户 Id 集合
     */
    private Set<Integer> _userIdSetCanHu;

    /**
     * 一炮多响
     */
    private boolean _yiPaoDuoXiang;

    /**
     * 可以亮风的用户 Id,
     */
    private int _userIdCanLiangFeng;

    /**
     * 亮风选择题
     */
    private LiangFengChoiceQuestion _liangFengChoiceQuestion;

    /**
     * 可以补风的用户 Id
     */
    private int _userIdCanBuFeng;

    /**
     * 对话队列
     */
    private Deque<Dialog> _dialogQ = null;

    /**
     * 类默认构造器
     */
    public ChiPengGangHuSession() {
        _fromOtherzT = null;
        _fromUserId = -1;
    }

    /**
     * 类参数构造器
     *
     * @param t          吃碰杠胡的是哪一张麻将牌
     * @param fromUserId 来自用户 Id
     */
    public ChiPengGangHuSession(MahjongTileDef t, int fromUserId) {
        _fromOtherzT = t;
        _fromUserId = fromUserId;
    }

    /**
     * 获取来自他人的麻将牌,
     * 这张牌可以且只能用作吃、碰、明杠、胡 ( 别人点炮我胡牌 )!
     * XXX 注意, 这些情况:
     * 亮风、补风、暗杠、补杠、自摸 ( 自己摸牌胡牌 ),
     * 会返回空值...
     *
     * @return 麻将牌
     */
    public MahjongTileDef getFromOtherzT() {
        return _fromOtherzT;
    }

    /**
     * 获取来自他人的麻将牌的整数值
     *
     * @return 整数值
     */
    public int getFromOtherzTIntVal() {
        return (null == _fromOtherzT) ? -1 : _fromOtherzT.getIntVal();
    }

    /**
     * 获取来自用户 Id,
     * 也就是 "来自他人的麻将牌 ( _fromOtherzT ) " 到底是从谁那里来的
     *
     * @return 用户 Id
     */
    public int getFromUserId() {
        return _fromUserId;
    }

    /**
     * 设置可以吃牌的用户 Id
     *
     * @param userId            用户 Id
     * @param chiChoiceQuestion 吃牌选择题
     */
    public void putUserIdCanChi(int userId, ChiChoiceQuestion chiChoiceQuestion) {
        _userIdCanChi = userId;
        _chiChoiceQuestion = chiChoiceQuestion;
        _dialogQ = null;
    }

    /**
     * 获取吃牌选择题
     *
     * @return 吃牌选择题
     */
    public ChiChoiceQuestion getChiChoiceQuestion() {
        return _chiChoiceQuestion;
    }

    /**
     * 设置可以碰牌的用户 Id
     *
     * @param userId 用户 Id
     */
    public void putUserIdCanPeng(int userId) {
        _userIdCanPeng = userId;
        _dialogQ = null;
    }

    /**
     * 设置可以明杠的用户 Id
     *
     * @param userId 用户 Id
     */
    public void putUserIdCanMingGang(int userId) {
        _userIdCanMingGang = userId;
        _dialogQ = null;
    }

    /**
     * 设置可以暗杠的用户 Id
     *
     * @param userId 用户 Id
     */
    public void putUserIdCanAnGang(int userId) {
        _userIdCanAnGang = userId;
        _dialogQ = null;
    }

    /**
     * 设置可以补杠的用户 Id
     *
     * @param userId 用户 Id
     */
    public void putUserIdCanBuGang(int userId) {
        _userIdCanBuGang = userId;
        _dialogQ = null;
    }

    /**
     * 设置可以自摸 ( 胡牌 ) 的用户 Id
     *
     * @param userId 用户 Id
     */
    public void putUserIdCanZiMo(int userId) {
        _userIdCanZiMo = userId;
        _dialogQ = null;
    }

    /**
     * 添加可以胡牌的用户 Id
     *
     * @param userId 用户 Id
     */
    public void addUserIdCanHu(int userId) {
        if (null == _userIdSetCanHu) {
            _userIdSetCanHu = new HashSet<>();
        }

        _userIdSetCanHu.add(userId);
        _dialogQ = null;
    }

    /**
     * 添加所有可以胡牌的用户 Id
     *
     * @param userIdSet 用户 Id 集合
     */
    public void addAllUserIdCanHu(Set<Integer> userIdSet) {
        if (null == _userIdSetCanHu) {
            _userIdSetCanHu = new HashSet<>();
        }

        _userIdSetCanHu.addAll(userIdSet);
        _dialogQ = null;
    }

    /**
     * 设置一炮多响
     *
     * @param val true = 是, false = 否
     */
    public void putYiPaoDuoXiang(boolean val) {
        _yiPaoDuoXiang = val;
    }

    /**
     * 设置可以亮风的用户 Id
     *
     * @param userId         用户 Id
     * @param choiceQuestion 亮风选择题
     */
    public void putUserIdCanLiangFeng(int userId, LiangFengChoiceQuestion choiceQuestion) {
        _userIdCanLiangFeng = userId;
        _liangFengChoiceQuestion = choiceQuestion;
    }

    /**
     * 获取亮风选择题
     *
     * @return 亮风选择题
     */
    public LiangFengChoiceQuestion getLiangFengChoiceQuestion() {
        return _liangFengChoiceQuestion;
    }

    /**
     * 设置可以补风的用户 Id
     *
     * @param userId 用户 Id
     */
    public void putUserIdCanBuFeng(int userId) {
        _userIdCanBuFeng = userId;
    }

    /**
     * 获取当前对话
     *
     * @return 对话
     */
    public Dialog getCurrDialog() {
        // 尝试构建对话队列
        tryBuildDialogQueue();
        // 获取第一个对话
        return _dialogQ.peekFirst();
    }

    /**
     * 移到下一对话
     */
    public void moveToNextDialog() {
        // 尝试构建对话队列
        tryBuildDialogQueue();
        // 删除第一个对话
        _dialogQ.pollFirst();
    }

    /**
     * 构建对话队列, XXX 注意: 优先级顺序是:
     * <ol>
     *     <li>胡</li>
     *     <li>亮风 ( 威海麻将特色玩法 )</li>
     *     <li>补风 ( 威海麻将特色玩法 )</li>
     *     <li>暗杠</li>
     *     <li>明杠</li>
     *     <li>碰</li>
     *     <li>吃</li>
     * </ol>
     */
    private void tryBuildDialogQueue() {
        if (null != _dialogQ) {
            return;
        }

        // 创建对话队列
        _dialogQ = new ArrayDeque<>(4);
        // 前一对话框
        Dialog prevD = null;

        if (null != _userIdSetCanHu &&
            _userIdSetCanHu.size() > 0) {

            // 获取当前牌局
            Round currRound = getCurrRoundByUserId(_fromUserId);

            if (null == currRound) {
                LOGGER.error(
                    "找不到当前牌局, fromUserId = {}",
                    _fromUserId
                );
                return;
            }

            // 获取来自玩家
            Player fromPlayer = currRound.getPlayerByUserId(_fromUserId);

            if (null == fromPlayer) {
                LOGGER.error(
                    "找不到来自玩家, fromUserId = {}",
                    _fromUserId
                );
                return;
            }

            // 如果有可以胡牌的用户,
            Dialog currD = null;

            for (int i = 0, seatIndex = fromPlayer.getSeatIndex(); i < currRound.getPlayerCount(); i++, seatIndex++) {
                // 根据座位索引获取玩家,
                // XXX 注意: 如果在创建房间的时候没有选择 "一炮多响",
                // 但是又出现了两家可以胡牌的情况,
                // 那么就需要按照逆时针顺序询问玩家是否胡牌...
                Player currPlayer = currRound.getPlayerBySeatIndex(seatIndex);

                if (null == currPlayer ||
                    !_userIdSetCanHu.contains(currPlayer.getUserId())) {
                    // 如果不能胡牌,
                    continue;
                }

                if (_yiPaoDuoXiang) {
                    // 如果支持一炮多响,
                    // 那么只使用一个对话来记录可以胡牌的用户 Id
                    if (null == currD) {
                        currD = new Dialog();
                        _dialogQ.add(currD);
                    }
                } else {
                    // 如果不支持一炮多响,
                    // 那么使用多个对话来记录可以胡牌的用户 Id
                    currD = new Dialog();
                    _dialogQ.add(currD);
                }

                currD.setUserIdCanOp(currPlayer.getUserId(), ChiPengGangHuDialog.OP_HU);
                prevD = currD;
            }
        }

        //
        // 接下来的代码,
        // 要做的就是创建吃、碰、杠的对话,
        // XXX 注意: 在创建对话的过程中会将对话进行合并!
        // 例如: 前一对话 ( prevD ) 是碰牌，
        //     且可以碰牌的用户 Id = 1001,
        //     当前对话 ( currD ) 是吃牌,
        //     且可以吃牌的用户 Id 也是 = 1001,
        //     那么当前会话就会 "合并" 到前一对话...
        //     具体的合并方式是: 令 currD = prevD, 然后再对 currD 进行操作
        //
        // 用户 Id 和可行的操作二维数组
        final int[][] userIdAndCanOpArrayArray = {
            // 麻将通用的操作, 顺序是: 自摸、补杠、暗杠、明杠、碰、吃
            { _userIdCanZiMo, ChiPengGangHuDialog.OP_ZI_MO, },
            { _userIdCanBuGang, ChiPengGangHuDialog.OP_BU_GANG, },
            { _userIdCanAnGang, ChiPengGangHuDialog.OP_AN_GANG, },
            { _userIdCanMingGang, ChiPengGangHuDialog.OP_MING_GANG, },
            { _userIdCanPeng, ChiPengGangHuDialog.OP_PENG, },
            { _userIdCanChi, ChiPengGangHuDialog.OP_CHI, },
            // 威海麻将特色玩法: 亮风和补风
            { _userIdCanLiangFeng, ChiPengGangHuDialog.OP_LIANG_FENG, },
            { _userIdCanBuFeng, ChiPengGangHuDialog.OP_BU_FENG, },
        };

        for (int[] userIdAndCanOpArray : userIdAndCanOpArrayArray) {
            // 获取当前用户 Id 和可行的操作
            final int currUserId = userIdAndCanOpArray[0];
            final int canOp = userIdAndCanOpArray[1];

            if (currUserId <= 0) {
                continue;
            }

            Dialog currD = null;

            if (null != prevD &&
                prevD.isJustOneUserId(currUserId)) {
                // 正好是前一对话中唯一的用户 Id,
                // 那么直接用前一对话即可 ( 以此达到合并对话的目的 )
                currD = prevD;
            }

            if (null == currD) {
                currD = new Dialog();
                _dialogQ.add(currD);
            }

            currD.setUserIdCanOp(currUserId, canOp);
            prevD = currD;
        }
    }

    /**
     * 根据用户 Id 获取当前牌局
     *
     * @param userId 用户 Id
     * @return 当前牌局
     */
    static private Round getCurrRoundByUserId(int userId) {
        // 获取当前房间
        Room currRoom = RoomGroup.getByUserId(userId);

        if (null != currRoom) {
            return currRoom.getCurrRound();
        } else {
            return null;
        }
    }

    /**
     * 吃碰杠胡对话
     */
    static public final class Dialog extends ChiPengGangHuDialog {
    }
}
