package org.mj.bizserver.mod.club.membercenter.bizdata;

import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;

import java.util.HashMap;
import java.util.Map;

/**
 * 固定玩法
 */
public final class FixGameX {
    /**
     * 索引
     */
    private int _index;

    /**
     * 游戏类型 0
     */
    private GameType0Enum _gameType0;

    /**
     * 游戏类型 1
     */
    private GameType1Enum _gameType1;

    /**
     * 规则字典
     */
    private Map<Integer, Integer> _ruleMap;

    /**
     * 获取索引
     *
     * @return 索引
     */
    public int getIndex() {
        return _index;
    }

    /**
     * 设置索引
     *
     * @param val 索引
     */
    public void setIndex(int val) {
        _index = val;
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
        return (null == _gameType0) ? -1 : _gameType0.getIntVal();
    }

    /**
     * 设置游戏类型 0
     *
     * @param val 游戏类型 0
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
        return (null == _gameType1) ? -1 : _gameType1.getIntVal();
    }

    /**
     * 设置游戏类型 1
     *
     * @param val 游戏类型 1
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
     * @param val 规则字典
     */
    public void setRuleMap(Map<Integer, Integer> val) {
        _ruleMap = val;
    }

    /**
     * 获取 JSON 字符串
     *
     * @return JSON 字符串
     */
    public String toJSONStr() {
        if (null == _gameType0 ||
            null == _gameType1 ||
            null == _ruleMap ||
            _ruleMap.isEmpty()) {
            return null;
        }

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("gameType0", _gameType0.getIntVal());
        jsonObj.put("gameType1", _gameType1.getIntVal());

        JSONObject joRuleMap = new JSONObject();
        jsonObj.put("ruleMap", joRuleMap);

        for (Map.Entry<Integer, Integer> entry : _ruleMap.entrySet()) {
            joRuleMap.put(
                String.valueOf(entry.getKey()),
                entry.getValue()
            );
        }

        return jsonObj.toString();
    }

    /**
     * 根据 JSON 字符串构建固定玩法
     *
     * @param jsonStr JSON 字符串
     * @return 固定玩法
     */
    static FixGameX fromJSONStr(String jsonStr) {
        return fromJSONObj(JSONObject.parseObject(jsonStr));
    }

    /**
     * 根据 JSON 对象构建固定玩法
     *
     * @param jsonObj JSON 对象
     * @return 固定玩法
     */
    static FixGameX fromJSONObj(JSONObject jsonObj) {
        if (null == jsonObj) {
            return null;
        }

        FixGameX bizObj = new FixGameX();
        bizObj.setGameType0(GameType0Enum.valueOf(jsonObj.getIntValue("gameType0")));
        bizObj.setGameType1(GameType1Enum.valueOf(jsonObj.getIntValue("gameType1")));

        // 获取规则字典
        JSONObject joRuleMap = jsonObj.getJSONObject("ruleMap");
        Map<Integer, Integer> ruleMap = new HashMap<>();

        for (String key : joRuleMap.keySet()) {
            ruleMap.put(Integer.parseInt(key), joRuleMap.getIntValue(key));
        }

        bizObj.setRuleMap(ruleMap);
        return bizObj;
    }
}
