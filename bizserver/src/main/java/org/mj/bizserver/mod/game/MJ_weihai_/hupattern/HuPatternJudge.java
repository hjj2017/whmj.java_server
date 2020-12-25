package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 胡牌模式法官
 */
public final class HuPatternJudge {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(HuPatternJudge.class);

    /**
     * 私有化类默认构造器
     */
    private HuPatternJudge() {
    }

    /**
     * 裁定胡牌模式,
     * XXX 注意: 将调用 Player#getCurrState#setHuPatternMap 修改胡牌模式字典!
     * XXX 注意: 要进行胡牌模式测试,
     * 必须满足 null != currPlayer.getCurrState().getHuPai() 这个条件!
     * 也就是说必须得记录胡牌...
     * 而这个胡牌是在 MJ_weihai_BizLogic#hu 函数中设置的.
     *
     * @param currRound  当前牌局
     * @param currPlayer 当前玩家
     */
    static public void judge(Round currRound, Player currPlayer) {
        if (null == currRound ||
            null == currPlayer) {
            return;
        }

        if (null == currPlayer.getCurrState().getMahjongZiMo() &&
            null == currPlayer.getCurrState().getMahjongHu()) {
            LOGGER.error(
                "玩家没有胡牌, userId = {}, atRoomId = {}, roundIndex = {}",
                currPlayer.getUserId(),
                currRound.getRoomId(),
                currRound.getRoundIndex()
            );
            return;
        }

        Map<Integer, Integer> huPatternMap = new HashMap<>();

        for (HuPatternDef def : HuPatternDef.values()) {
            if (def.getPatternTest().test(currRound, currPlayer)) {
                huPatternMap.put(
                    def.getIntVal(), def.getFan()
                );
            }
        }

        if (huPatternMap.size() >= 2) {
            // 移除平胡
            huPatternMap.remove(HuPatternDef.PING_HU.getIntVal());
        }

        if (huPatternMap.containsKey(HuPatternDef.JIA_WU.getIntVal())) {
            // 夹五和夹胡不能叠加
            huPatternMap.remove(HuPatternDef.JIA_HU.getIntVal());
        }

        HuPatternDef[] qiXiaoDuiArray = {
            HuPatternDef.QI_XIAO_DUI,
            HuPatternDef.HAO_HUA_QI_XIAO_DUI,
            HuPatternDef.SHUANG_HAO_HUA_QI_XIAO_DUI,
            HuPatternDef.CHAO_HAO_HUA_QI_XIAO_DUI,
        };

        boolean delOtherQiXiaoDui = false;

        for (int i = qiXiaoDuiArray.length - 1; i >= 0; i--) {
            // 获取当前七小对模式
            HuPatternDef qiXiaoDui = qiXiaoDuiArray[i];

            if (delOtherQiXiaoDui) {
                // 七小对不能叠加
                huPatternMap.remove(qiXiaoDui.getIntVal());
            } else if (huPatternMap.containsKey(qiXiaoDui.getIntVal())) {
                delOtherQiXiaoDui = true;
            }
        }

        LOGGER.info(
            "最终判定胡牌模式, userId = {}, atRoomId = {}, roundIndex = {}, huPattern = {}",
            currPlayer.getUserId(),
            currRound.getRoomId(),
            currRound.getRoundIndex(),
            huPatternMap.keySet()
        );

        currPlayer.getSettlementResult().setHuPatternMap(huPatternMap);
    }
}
