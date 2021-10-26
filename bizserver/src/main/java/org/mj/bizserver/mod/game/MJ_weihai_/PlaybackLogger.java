package org.mj.bizserver.mod.game.MJ_weihai_;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.mj.bizserver.base.AliOSSZervice;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.report.IWordz;
import org.mj.comm.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * 回放数据日志
 */
final class PlaybackLogger {
    /**
     * 日志对象
     */
    static private final Logger LOGGER = LoggerFactory.getLogger(PlaybackLogger.class);

    /**
     * 私有化类默认构造器
     */
    private PlaybackLogger() {
    }

    /**
     * 保存回放数据到阿里 OSS
     *
     * @param roomCreateTime 房间创建时间
     * @param currRound      当前牌局
     */
    static void savePlaybackToAliOSS(final long roomCreateTime, final Round currRound) {
        if (roomCreateTime <= 0 ||
            null == currRound) {
            return;
        }

        final String dateTimeStr = DateTimeUtil.getDateTimeStr(
            roomCreateTime,
            "yyyy/MM/dd/HH"
        );

        final String path = MessageFormat.format(
            "{0}/{1}_{2}.json",
            dateTimeStr,
            String.valueOf(currRound.getRoomId()),
            currRound.getRoundIndex()
        );

        final JSONObject joROOT = new JSONObject(true);

        // 填充当前牌局
        fillCurrRound(joROOT, currRound);
        // 填充玩家列表
        fillPlayerList(joROOT, currRound);
        // 填充字条列表
        fillWordzListList(joROOT, currRound);

        LOGGER.info(
            "保存回放数据到阿里 OSS, path = {}",
            path
        );

        // 保存回放数据
        AliOSSZervice.getInstance().saveText(path, joROOT.toString(
            SerializerFeature.QuoteFieldNames,
            SerializerFeature.WriteBigDecimalAsPlain
        ));
        // 清理回放字条列表
        currRound.clearPlaybackWordzListList();
    }

    /**
     * 填充当前牌局
     *
     * @param joROOT    根节点
     * @param currRound 当前牌局
     */
    static private void fillCurrRound(final JSONObject joROOT, final Round currRound) {
        if (null == joROOT ||
            null == currRound) {
            return;
        }

        final JSONObject joRound = new JSONObject(true);
        joROOT.put("currRound", joRound);

        joRound.put("roomId", currRound.getRoomId());
        joRound.put("roundIndex", currRound.getRoundIndex());

        // 获取规则字典
        final Map<Integer, Integer> ruleMap = currRound.getRuleSetting().getInnerMap();
        final JSONObject joRuleSetting = new JSONObject(true);
        joRound.put("ruleSetting", joRuleSetting);

        for (Map.Entry<Integer, Integer> entry : ruleMap.entrySet()) {
            joRuleSetting.put(
                String.valueOf(entry.getKey()),
                entry.getValue()
            );
        }
    }

    /**
     * 填充玩家列表
     *
     * @param joROOT    根节点
     * @param currRound 当前牌局
     */
    static private void fillPlayerList(final JSONObject joROOT, final Round currRound) {
        if (null == joROOT ||
            null == currRound) {
            return;
        }

        final JSONArray joPlayerArray = new JSONArray();
        joROOT.put("playerList", joPlayerArray);

        for (Player currPlayer : currRound.getPlayerListCopy()) {
            if (null == currPlayer) {
                continue;
            }

            JSONObject joPlayer = new JSONObject(true);
            joPlayer.put("userId", currPlayer.getUserId());
            joPlayer.put("userName", currPlayer.getUserName());
            joPlayer.put("headImg", currPlayer.getHeadImg());
            joPlayer.put("sex", currPlayer.getSex());
            joPlayer.put("seatIndex", currPlayer.getSeatIndex());
            joPlayer.put("currScore", currPlayer.getCurrScore());
            joPlayer.put("totalScore", currPlayer.getTotalScore());

            joPlayerArray.add(joPlayer);
        }
    }

    /**
     * 填充字条列表
     *
     * @param joROOT    JSON 根节点
     * @param currRound 当前牌局
     */
    static private void fillWordzListList(
        final JSONObject joROOT, final Round currRound) {
        if (null == joROOT ||
            null == currRound) {
            return;
        }

        final JSONArray joWordzArrayArray = new JSONArray();
        joROOT.put("wordzListList", joWordzArrayArray);

        for (List<IWordz> wordzList : currRound.getPlaybackWordzListList()) {
            // 创建字条列表 JSON
            final JSONArray joWordzArray = new JSONArray();

            for (IWordz w : wordzList) {
                if (null == w) {
                    continue;
                }

                // 构建字条 JSON
                final JSONObject joWordz = w.buildJSONObj();

                if (null != joWordz &&
                    !joWordz.isEmpty()) {
                    joWordzArray.add(joWordz);
                }
            }

            if (!joWordzArray.isEmpty()) {
                joWordzArrayArray.add(joWordzArray);
            }
        }
    }
}
