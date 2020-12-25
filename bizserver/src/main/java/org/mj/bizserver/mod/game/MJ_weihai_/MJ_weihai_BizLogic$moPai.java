package org.mj.bizserver.mod.game.MJ_weihai_;

import org.mj.bizserver.foundation.BizResultWrapper;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiPengGangHuSession;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.LiangFengChoiceQuestion;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RoomGroup;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.hupattern.HuFormula;
import org.mj.bizserver.mod.game.MJ_weihai_.report.ReporterTeam;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongChiPengGangHuOpHint;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongHuangZhuang;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongLiangGangDing;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_MahjongMoPai;
import org.mj.bizserver.mod.game.MJ_weihai_.report.Wordz_RedirectActUserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 摸牌,
 * XXX 注意: 自己摸牌之后, 只可能执行以下几个逻辑中的一个:
 * <ol>
 *     <li>胡牌</li>
 *     <li>暗杠</li>
 *     <li>补杠</li>
 *     <li>出牌</li>
 *     <li>亮风或补风 ( 威海地区的特殊玩法 )</li>
 * </ol>
 * <p>
 * 都是自己的行为,
 * 到目前为止 (20200627) 威海麻将还没有听牌,
 * 如果可以听牌,
 * 也是在这里实现...
 */
interface MJ_weihai_BizLogic$moPai {
    /**
     * 日志对象
     */
    Logger LOGGER = LoggerFactory.getLogger(MJ_weihai_BizLogic$moPai.class);

    /**
     * 摸牌
     *
     * @param roomId 房间 Id
     */
    default void moPai(final int roomId, final BizResultWrapper<ReporterTeam> resultX) {
        if (roomId <= 0 ||
            null == resultX) {
            return;
        }

        // 获取当前房间
        Room currRoom = RoomGroup.getByRoomId(roomId);

        if (null == currRoom) {
            LOGGER.error(
                "当前房间为空, roomId = {}",
                roomId
            );
            return;
        }

        // 获取当前牌局
        Round currRound = currRoom.getCurrRound();

        if (null == currRound) {
            LOGGER.error(
                "当前牌局为空, roomId = {}",
                roomId
            );
            return;
        }

        // 获取当前执行摸牌操作的玩家
        Player execPlayer = currRound.getCurrActPlayer();

        if (null == execPlayer) {
            LOGGER.error(
                "当前执行摸牌操作的玩家为空, atRoomId = {}, roundIndex = {}",
                roomId,
                currRound.getRoundIndex()
            );
            return;
        }

        if (null != execPlayer.getMoPai()) {
            LOGGER.error(
                "当前玩家已经摸牌, userId = {}, atRoomId = {}, roundIndex = {}, moPai = {}",
                execPlayer.getUserId(),
                roomId,
                currRound.getRoundIndex(),
                execPlayer.getMoPaiIntVal()
            );
            return;
        }

        MahjongTileDef moPai;

        // 是否不荒庄
        boolean isBuHuangZhuang = currRoom.getRuleSetting().isBuHuangZhuang();

        if (!isBuHuangZhuang &&
            currRound.getRemainCardNum() <= 14) {
            // 如果没有勾选不荒庄,
            // 并且当前剩余牌张 <= 14 张,
            // 那就摸不出牌了
            moPai = null;
        } else {
            // 如果勾选了不荒庄,
            // 或者当前剩余牌张 > 14 张,
            // 那就摸出一张牌...
            //
            if (execPlayer.getCurrState().getJustGangNum() > 0 &&
                currRoom.getRuleSetting().isLiangGangDing()) {
                // 如果当前玩家刚刚杠完牌,
                // 并且在创建房间时勾选了 "亮杠腚" 选项,
                // 那么从亮杠腚中抓牌...
                moPai = currRound.takeLiangGangDingT();

                LOGGER.info(
                    "亮杠腚抓牌, userId = {}, atRoomId = {}, roundIndex = {}, moPai = {}",
                    execPlayer.getUserId(),
                    currRoom.getRoomId(),
                    currRound.getRoundIndex(),
                    moPai.getIntVal()
                );
            } else {
                // 从麻将队列里抓牌...
                moPai = currRound.takeAMahjongTile();
            }
        }

        ReporterTeam rptrTeam = resultX.getFinalResult();

        if (null == rptrTeam) {
            rptrTeam = new ReporterTeam(currRoom.getRoomId());
            resultX.setFinalResult(rptrTeam);
        }

        if (currRoom.getRuleSetting().isLiangGangDing()) {
            rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(
                new Wordz_MahjongLiangGangDing(currRound.getLiangGangDingT0())
            ));
        }

        if (null == moPai) {
            LOGGER.warn(
                "摸牌为空, 牌局结束! userId = {}, atRoomId = {}, roundIndex = {}",
                execPlayer.getUserId(),
                roomId,
                currRound.getRoundIndex()
            );
            currRound.setEnded(true);
            rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(
                new Wordz_MahjongHuangZhuang()
            ));

            // 当牌局结束
            MJ_weihai_BizLogic.getInstance().onCurrRoundEnded(
                currRoom, currRound
            );
            return;
        }

        // 设置摸牌
        execPlayer.setMoPai(moPai);

        LOGGER.info(
            "玩家摸牌, userId = {}, userName = {}, atRoomId = {}, roundIndex = {}, mahjongInHand = {}, moPai = {}",
            execPlayer.getUserId(),
            execPlayer.getUserName(),
            currRoom.getRoomId(),
            currRound.getRoundIndex(),
            execPlayer.getMahjongInHandIntValList(),
            moPai.getIntVal()
        );

        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(
            new Wordz_RedirectActUserId(
                currRound.getCurrActPlayer().getUserId(),
                currRound.getCurrActPlayer().getSeatIndex(),
                currRound.getRoundIndex(),
                currRound.getRemainCardNum(),
                -1
            )
        ));

        rptrTeam.addPublicWordz(rptrTeam.addPlaybackWordz(rptrTeam.addPrivateWordz(
            new Wordz_MahjongMoPai(
                execPlayer.getUserId(), moPai
            )
        )).createMaskCopy());

        // 查找可以亮风的麻将牌集合
        final Set<MahjongTileDef> canLiangFengTSet = findMahjongCanLiangFeng(currRound, execPlayer);
        // 是否可以亮风
        final boolean canLiangFeng = (null != canLiangFengTSet && !canLiangFengTSet.isEmpty());
        // 是否可以补风
        final boolean canBuFeng = (null != MJ_weihai_BizLogic.getInstance().findMahjongCanBuFeng(execPlayer));

        // 玩家是否可以自摸 ( 胡牌 )
        final boolean canZiMo = HuFormula.test(
            execPlayer.getMahjongInHandCopy(),
            moPai
        );

        // 是否可以补杠
        final boolean canBuGang = execPlayer.canBuGang(moPai);
        // 看看玩家可以暗杠的麻将牌是否为空
        final boolean canAnGang = (null != MJ_weihai_BizLogic.getInstance().findMahjongCanAnGang(execPlayer));

        if (canLiangFeng ||
            canBuFeng ||
            canZiMo ||
            canBuGang ||
            canAnGang) {
            // 如果是可以胡牌或者暗杠,
            // 创建吃碰杠胡会话
            final ChiPengGangHuSession sessionObj = new ChiPengGangHuSession();

            // 亮风选择题
            LiangFengChoiceQuestion liangFengChoiceQuestion = null;

            if (null != canLiangFengTSet &&
                !canLiangFengTSet.isEmpty()) {
                // 创建亮风选择题
                liangFengChoiceQuestion = new LiangFengChoiceQuestion(
                    currRound.getRuleSetting().isLuanMao(),
                    canLiangFengTSet
                );

                // 可以亮风
                sessionObj.putUserIdCanLiangFeng(
                    execPlayer.getUserId(), liangFengChoiceQuestion
                );
            }

            if (canBuFeng) {
                // 可以补风
                sessionObj.putUserIdCanBuFeng(
                    execPlayer.getUserId()
                );
            }

            if (canZiMo) {
                // 可以自摸
                sessionObj.putUserIdCanZiMo(execPlayer.getUserId());
            }

            if (canBuGang) {
                // 可以补杠
                sessionObj.putUserIdCanBuGang(
                    execPlayer.getUserId()
                );
            }

            if (canAnGang) {
                sessionObj.putUserIdCanAnGang(
                    execPlayer.getUserId()
                );
            }

            rptrTeam.addPrivateWordz(new Wordz_MahjongChiPengGangHuOpHint(
                execPlayer.getUserId(),
                canBuGang || canAnGang,
                canZiMo
            ).putOpHintLiangFeng(canLiangFeng, liangFengChoiceQuestion)
                .putOpHintBuFeng(canBuFeng));

            // 设置到当前牌局
            currRound.setChiPengGangHuSession(sessionObj);

            LOGGER.info(
                "准备给用户推送 \"吃碰杠胡操作\" 提示, userId = {}, atRoomId = {}, roundIndex = {}, 吃 = {}, 碰 = {}, 杠 = {}, 胡 = {}, 亮风 = {}, 补风 = {}",
                execPlayer.getUserId(),
                currRound.getRoomId(),
                currRound.getRoundIndex(),
                false, // 吃
                false, // 碰, 吃和碰只能从别人那里来, 摸牌的时候不可能发生
                canBuGang || canAnGang,
                canZiMo,
                null != canLiangFengTSet,
                canBuFeng
            );
        }
    }

    /**
     * 查找可以亮风的麻将牌列表
     *
     * @param currRound  当前牌局
     * @param currPlayer 当前玩家
     * @return 麻将牌列表
     */
    static private Set<MahjongTileDef> findMahjongCanLiangFeng(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currRound.getRuleSetting() ||
            null == currPlayer) {
            return null;
        }

        // 获取摸牌
        final MahjongTileDef moPai = currPlayer.getMoPai();

        if (null == moPai) {
            return null;
        }

        if (currPlayer.getMahjongOutput().size() >= 1 ||
            null != currPlayer.getMahjongLiangFeng().getKind()) {
            // 如果已有打出的麻将牌,
            // 或者如果已经亮过风,
            // 那么就不能再亮风了...
            return null;
        }

        // 获取麻将手牌列表
        final List<MahjongTileDef> mahjongInHand = currPlayer.getMahjongInHandCopy();
        // 可以亮风的麻将牌集合
        final Set<MahjongTileDef> tSet_feng = new HashSet<>();
        final Set<MahjongTileDef> tSet_jian = new HashSet<>();

        if (moPai.getSuit() == MahjongTileDef.Suit.FENG) {
            tSet_feng.add(moPai);
        } else if (moPai.getSuit() == MahjongTileDef.Suit.JIAN) {
            tSet_jian.add(moPai);
        }

        for (MahjongTileDef currT : mahjongInHand) {
            if (currT.getSuit() == MahjongTileDef.Suit.FENG) {
                tSet_feng.add(currT);
            } else if (currT.getSuit() == MahjongTileDef.Suit.JIAN) {
                tSet_jian.add(currT);
            }
        }

        if (currRound.getRuleSetting().isLuanMao()) {
            //
            // 如果创建房间时勾选了乱锚,
            // 那么就看看 "风牌 ( 东南西北 ) + 箭牌 ( 中发白 )" 是不是 >= 3 张?
            // 如果 >= 3 张,
            // 就可以亮风...
            if ((tSet_feng.size() + tSet_jian.size()) >= 3) {
                // 把箭牌加到风牌集合里,
                // 省得再创建一个集合浪费内存...
                tSet_feng.addAll(tSet_jian);
                return tSet_feng;
            } else {
                return null;
            }
        }

        // 如果创建房间时没有勾选乱锚,
        // 那就单独看看风牌 ( 东南西北 ) 或者箭牌 ( 中发白 ) 是不是 >= 3 张?
        if (tSet_feng.size() >= 3 &&
            tSet_jian.size() >= 3) {
            // 如果风牌 ( 东南西北 ) 和箭牌 ( 中发白 ) 数量都 >= 3 张,
            // 那么把箭牌加到风牌集合里,
            // 省得再创建一个集合浪费内存...
            tSet_feng.addAll(tSet_jian);
            return tSet_feng;
        }

        if (tSet_feng.size() >= 3) {
            return tSet_feng;
        } else if (tSet_jian.size() >= 3) {
            return tSet_jian;
        }

        return null;
    }
}
