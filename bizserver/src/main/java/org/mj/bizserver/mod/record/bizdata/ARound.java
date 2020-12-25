package org.mj.bizserver.mod.record.bizdata;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.mod.record.dao.RoundLogEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 单个牌局
 */
public final class ARound {
    /**
     * 牌局日志实体
     */
    private final RoundLogEntity _roundLogEntity;

    /**
     * 玩家列表
     */
    private List<Player> _playerList;

    /**
     * 类参数构造器
     *
     * @param roundLogEntity 牌局日志实体
     */
    public ARound(RoundLogEntity roundLogEntity) {
        if (null == roundLogEntity) {
            throw new IllegalArgumentException("roundLogEntity is null");
        }

        _roundLogEntity = roundLogEntity;
    }

    /**
     * 获取牌局索引
     *
     * @return 牌局索引
     */
    public int getRoundIndex() {
        return _roundLogEntity.getRoundIndex();
    }

    /**
     * 获取牌局创建时间
     *
     * @return 牌局创建时间
     */
    public long getCreateTime() {
        return _roundLogEntity.getCreateTime();
    }

    /**
     * 获取回放存根
     *
     * @return 回放存根
     */
    public String getPlaybackStub() {
        return _roundLogEntity.getPlaybackStub();
    }

    /**
     * 获取玩家列表
     *
     * @return 玩家列表
     */
    public List<Player> getPlayerList() {
        if (null != _playerList) {
            return _playerList;
        }

        // 获取所有玩家
        final JSONObject joAllPlayer = JSONObject.parseObject(_roundLogEntity.getAllPlayer());
        // 获取所有当前分数
        final JSONObject joAllCurrScore = JSONObject.parseObject(_roundLogEntity.getAllCurrScore());

        if (null == joAllPlayer ||
            joAllPlayer.isEmpty() ||
            null == joAllCurrScore ||
            joAllCurrScore.isEmpty()) {
            return null;
        }

        final List<Player> playerList = new ArrayList<>(8);

        for (String strUserId : joAllPlayer.keySet()) {
            // 获取玩家 JSON 对象
            JSONObject joPlayer = joAllPlayer.getJSONObject(strUserId);

            if (null != joPlayer) {
                // 转成 Java 对象
                Player currPlayer = joPlayer.toJavaObject(Player.class);

                playerList.add(currPlayer);
                currPlayer.setCurrScore(joAllCurrScore.getIntValue(strUserId));
            }
        }

        // 按照座位索引排序
        playerList.sort(Comparator.comparingInt(Player::getSeatIndex));

        return _playerList = playerList;
    }
}
