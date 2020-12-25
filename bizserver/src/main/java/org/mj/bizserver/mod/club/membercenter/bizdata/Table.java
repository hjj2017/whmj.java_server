package org.mj.bizserver.mod.club.membercenter.bizdata;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ( 亲友圈 ) 牌桌
 */
public final class Table {
    /**
     * 桌号
     */
    private int _seqNum = -1;

    /**
     * 房间 Id
     */
    private int _roomId = -1;

    /**
     * 房间 UUId
     */
    private String _roomUUId = null;

    /**
     * 游戏类型 0
     */
    private GameType0Enum _gameType0 = null;

    /**
     * 游戏类型 1
     */
    private GameType1Enum _gameType1 = null;

    /**
     * 规则字典
     */
    private Map<Integer, Integer> _ruleMap = null;

    /**
     * 最大局数
     */
    private int _maxRound = -1;

    /**
     * 当前局数
     */
    private int _currRound = -1;

    /**
     * 最大玩家数量
     */
    private int _maxPlayer = -1;

    /**
     * 玩家列表
     */
    private List<Player> _playerList = null;

    /**
     * 获取桌号
     *
     * @return 桌号
     */
    public int getSeqNum() {
        return _seqNum;
    }

    /**
     * 设置桌号
     *
     * @param val 整数值
     */
    public void setSeqNum(int val) {
        _seqNum = val;
    }

    /**
     * 获取房间 Id
     *
     * @return 房间 Id
     */
    public int getRoomId() {
        return _roomId;
    }

    /**
     * 设置房间 Id
     *
     * @param val 整数值
     */
    public void setRoomId(int val) {
        _roomId = val;
    }

    /**
     * 获取房间 UUId
     *
     * @return 房间 UUId
     */
    public String getRoomUUId() {
        return _roomUUId;
    }

    /**
     * 设置房间 UUId
     *
     * @param val 字符串值
     */
    public void setRoomUUId(String val) {
        _roomUUId = val;
    }

    /**
     * 获取游戏类型 0
     *
     * @return 游戏类型 0
     */
    public GameType0Enum getGameType0() {
        return _gameType0;
    }

    /**
     * 获取游戏类型 0 整数值
     *
     * @return 游戏类型 0 整数值
     */
    public int getGameType0IntVal() {
        return (null == getGameType0()) ? -1 : getGameType0().getIntVal();
    }

    /**
     * 设置游戏类型 0
     *
     * @param val 枚举值
     */
    public void setGameType0(GameType0Enum val) {
        _gameType0 = val;
    }

    /**
     * 获取游戏类型 1
     *
     * @return 游戏类型 1
     */
    public GameType1Enum getGameType1() {
        return _gameType1;
    }

    /**
     * 获取游戏类型 1 整数值
     *
     * @return 游戏类型 1 整数值
     */
    public int getGameType1IntVal() {
        return (null == getGameType1()) ? -1 : getGameType1().getIntVal();
    }

    /**
     * 设置游戏类型 1
     *
     * @param val 枚举值
     */
    public void setGameType1(GameType1Enum val) {
        _gameType1 = val;
    }

    /**
     * 获取规则字典
     *
     * @return 规则字典
     */
    public Map<Integer, Integer> getRuleMap() {
        return _ruleMap;
    }

    /**
     * 设置规则字典
     *
     * @param val 字典值
     */
    public void setRuleMap(Map<Integer, Integer> val) {
        _ruleMap = val;
    }

    /**
     * 添加规则条目
     *
     * @param key 关键字
     * @param val 数值
     */
    public void addRuleItem(int key, int val) {
        if (key <= 0) {
            return;
        }

        if (null == _ruleMap) {
            _ruleMap = new ConcurrentHashMap<>();
        }

        _ruleMap.putIfAbsent(key, val);
    }

    /**
     * 获取最大局数
     *
     * @return 最大局数
     */
    public int getMaxRound() {
        return _maxRound;
    }

    /**
     * 设置最大局数
     *
     * @param val 整数值
     */
    public void setMaxRound(int val) {
        _maxRound = val;
    }

    /**
     * 获取当前局数
     *
     * @return 当前局数
     */
    public int getCurrRound() {
        return _currRound;
    }

    /**
     * 设置当前局数
     *
     * @param val 整数值
     */
    public void setCurrRound(int val) {
        _currRound = val;
    }

    /**
     * 获取最大玩家数量
     *
     * @return 最大玩家数量
     */
    public int getMaxPlayer() {
        return _maxPlayer;
    }

    /**
     * 设置最大玩家数量
     *
     * @param val 整数值
     */
    public void setMaxPlayer(int val) {
        _maxPlayer = val;
    }

    /**
     * 获取玩家列表
     *
     * @return 玩家列表
     */
    public List<Player> getPlayerList() {
        return _playerList;
    }

    /**
     * 设置玩家列表
     *
     * @param val 列表对象
     */
    public void setPlayerList(List<Player> val) {
        _playerList = val;
    }

    /**
     * 添加玩家
     *
     * @param p 玩家
     */
    public void addPlayer(Player p) {
        if (null == p) {
            return;
        }

        if (null == _playerList) {
            _playerList = new ArrayList<>();
        }

        _playerList.add(p);
    }

    /**
     * 从 JSON 对象中创建牌桌
     *
     * @param joRoot JSON 对象
     * @return 牌桌
     */
    static public Table fromJSON(JSONObject joRoot) {
        if (null == joRoot ||
            joRoot.isEmpty()) {
            return null;
        }

        final Table newTable = new Table();

        newTable.setSeqNum(joRoot.getIntValue("tableSeqNum"));
        newTable.setRoomId(joRoot.getIntValue("roomId"));
        newTable.setRoomUUId(joRoot.getString("roomUUId"));
        newTable.setGameType0(GameType0Enum.valueOf(joRoot.getIntValue("gameType0")));
        newTable.setGameType1(GameType1Enum.valueOf(joRoot.getIntValue("gameType1")));
        newTable.setMaxRound(joRoot.getIntValue("maxRound"));
        newTable.setCurrRound(joRoot.getIntValue("currRound"));
        newTable.setMaxPlayer(joRoot.getIntValue("maxPlayer"));

        JSONObject joRuleMap = joRoot.getJSONObject("ruleMap");

        if (null != joRuleMap) {
            joRuleMap.forEach((strKey, oVal) -> {
                if (null == oVal) {
                    return;
                }

                newTable.addRuleItem(
                    Integer.parseInt(strKey),
                    (Integer) oVal
                );
            });
        }

        // 获取 JSON 数组
        JSONArray jaPlayerArray = joRoot.getJSONArray("playerArray");

        for (int i = 0; i < jaPlayerArray.size(); i++) {
            // 从 JSON 中创建玩家
            Player newPlayer = Player.fromJSON(jaPlayerArray.getJSONObject(i));

            if (null != newPlayer) {
                newTable.addPlayer(newPlayer);
            }
        }

        return newTable;
    }
}
