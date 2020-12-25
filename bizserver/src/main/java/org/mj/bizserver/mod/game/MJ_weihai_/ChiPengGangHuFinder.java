package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiChoiceQuestion;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.hupattern.HuFormula;
import org.mj.comm.util.OutParam;

import java.util.HashSet;
import java.util.Set;

/**
 * 吃碰杠发现者,
 * XXX 注意: 这个类是作为 "除我之外查找其他人的吃碰杠胡" 算法,
 * 如果自己摸牌之后想看看能不能胡牌,
 * 直接调用 HuFormula 即可...
 *
 * @see HuFormula
 */
final class ChiPengGangHuFinder {
    /**
     * 私有化类默认构造器
     */
    private ChiPengGangHuFinder() {
    }

    /**
     * 在当前牌局中查找可以吃的用户 Id
     *
     * @param currRound             当前牌局
     * @param t                     要吃的牌
     * @param fromUserId            这张牌是谁打出来的
     * @param out_chiChoiceQuestion ( 输出参数 ) 吃牌选择题
     * @return 用户 Id
     */
    static public int findUserIdCanChi(Round currRound, MahjongTileDef t, int fromUserId, OutParam<ChiChoiceQuestion> out_chiChoiceQuestion) {
        if (null == currRound ||
            null == t) {
            return -1;
        }

        // 获取当前玩家
        Player currPlayer = currRound.getPlayerByUserId(fromUserId);

        if (null == currPlayer) {
            return -1;
        }

        // 获取下一个位置的玩家, 也就是下家
        Player nextPlayer = currRound.getPlayerBySeatIndex(currPlayer.getSeatIndex() + 1);

        if (null == nextPlayer) {
            return -1;
        }

        if (null == out_chiChoiceQuestion) {
            out_chiChoiceQuestion = new OutParam<>();
        }

        MahjongTileDef t0, t1;
        boolean caseA = false, caseB = false, caseC = false;

        // 第一种吃牌情况: t0, t1, t
        t0 = MahjongTileDef.valueOf(t.getIntVal() - 2);
        t1 = MahjongTileDef.valueOf(t.getIntVal() - 1);

        if (null != t0 &&
            null != t1 &&
            nextPlayer.hasAMahjongTileInHand(t0) &&
            nextPlayer.hasAMahjongTileInHand(t1)) {
            caseA = true;
        }

        // 第二种吃牌情况: t0, t, t1
        t0 = MahjongTileDef.valueOf(t.getIntVal() - 1);
        t1 = MahjongTileDef.valueOf(t.getIntVal() + 1);

        if (null != t0 &&
            null != t1 &&
            nextPlayer.hasAMahjongTileInHand(t0) &&
            nextPlayer.hasAMahjongTileInHand(t1)) {
            caseB = true;
        }

        // 第三种吃牌情况: t, t0, t1
        t0 = MahjongTileDef.valueOf(t.getIntVal() + 1);
        t1 = MahjongTileDef.valueOf(t.getIntVal() + 2);

        if (null != t0 &&
            null != t1 &&
            nextPlayer.hasAMahjongTileInHand(t0) &&
            nextPlayer.hasAMahjongTileInHand(t1)) {
            caseC = true;
        }

        if (caseA ||
            caseB ||
            caseC) {
            out_chiChoiceQuestion.setVal(new ChiChoiceQuestion(t, caseA, caseB, caseC));
            return nextPlayer.getUserId();
        } else {
            return -1;
        }
    }

    /**
     * 在当前牌局中查找可以碰牌的用户 Id
     *
     * @param currRound  当前牌局
     * @param t          要碰的麻将牌
     * @param fromUserId 这张牌是谁打出来的
     * @return 用户 Id
     */
    static public int findUserIdCanPeng(Round currRound, MahjongTileDef t, int fromUserId) {
        if (null == currRound ||
            null == t) {
            return -1;
        }

        // 获取玩家数量
        final int playerCount = currRound.getPlayerCount();

        for (int i = 0; i < playerCount; i++) {
            // 获取当前玩家
            Player currPlayer = currRound.getPlayerBySeatIndex(i);

            if (null == currPlayer ||
                currPlayer.getUserId() == fromUserId) {
                // 如果当前玩家为空,
                // 或者当前玩家就是出牌者自己...
                continue;
            }

            // 获取麻将数量
            int tCount = 0;

            for (MahjongTileDef aMahjongTile : currPlayer.getMahjongInHandCopy()) {
                if (aMahjongTile == t) {
                    ++tCount;
                }
            }

            if (tCount >= 2) {
                return currPlayer.getUserId();
            }
        }

        return -1;
    }

    /**
     * 在当前牌局中查找可以明杠的用户 Id
     *
     * @param currRound  当前牌局
     * @param t          要杠的牌
     * @param fromUserId 这张牌是谁打出来的
     * @return 用户 Id
     */
    static public int findUserIdCanMingGang(Round currRound, MahjongTileDef t, int fromUserId) {
        if (null == currRound ||
            null == t) {
            return -1;
        }

        // 获取玩家数量
        final int playerCount = currRound.getPlayerCount();

        for (int i = 0; i < playerCount; i++) {
            // 获取当前玩家
            Player currPlayer = currRound.getPlayerBySeatIndex(i);

            if (null == currPlayer ||
                currPlayer.getUserId() == fromUserId) {
                // 如果当前玩家为空,
                // 或者当前玩家就是出牌者自己...
                continue;
            }

            // 获取麻将数量
            int tCount = 0;

            for (MahjongTileDef aMahjongTile : currPlayer.getMahjongInHandCopy()) {
                if (aMahjongTile == t) {
                    ++tCount;
                }
            }

            if (tCount >= 3) {
                return currPlayer.getUserId();
            }
        }

        return -1;
    }

    /**
     * 查找可以胡牌的用户 Id 集合
     *
     * @param currRound  当前牌局
     * @param t          麻将牌
     * @param fromUserId 来自用户 Id
     * @return 用户 Id 集合
     */
    static Set<Integer> findUserIdSetCanHu(Round currRound, MahjongTileDef t, int fromUserId) {
        if (null == currRound ||
            null == t) {
            return null;
        }

        // 用户 Id 集合
        final Set<Integer> userIdSet = new HashSet<>();
        // 获取玩家数量
        final int playerCount = currRound.getPlayerCount();

        for (int i = 0; i < playerCount; i++) {
            // 获取当前玩家
            Player currPlayer = currRound.getPlayerBySeatIndex(i);

            if (null == currPlayer ||
                currPlayer.getUserId() == fromUserId) {
                // 如果当前玩家为空,
                // 或者当前玩家就是出牌者自己...
                continue;
            }

            if (HuFormula.test(currPlayer.getMahjongInHandCopy(), t)) {
                userIdSet.add(
                    currPlayer.getUserId()
                );
            }
        }

        return userIdSet;
    }
}
