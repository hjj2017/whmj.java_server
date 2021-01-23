package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.mj.bizserver.def.GameType0Enum;
import org.mj.bizserver.def.GameType1Enum;
import org.mj.bizserver.mod.game.MJ_weihai_.timertask.ITimerTask;
import org.mj.comm.util.DateTimeUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 麻将房间
 */
public final class Room {
    /**
     * 房间 UUId
     */
    private String _roomUUId;

    /**
     * 房间 Id
     */
    private final int _roomId;

    /**
     * 规则设置
     */
    private final RuleSetting _ruleSetting;

    /**
     * 房主用户 Id
     */
    private final int _ownerId;

    /**
     * 创建时间
     */
    private long _createTime;

    /**
     * 亲友圈 Id
     */
    private int _clubId = -1;

    /**
     * 牌桌序号
     */
    private int _tableSeqNum = -1;

    /**
     * 玩家列表
     */
    private final List<Player> _playerList = new ArrayList<>();

    /**
     * 玩家列表副本
     */
    private List<Player> _playerListCopy = null;

    /**
     * 用户 Id 黑名单
     */
    private Set<Integer> _userIdBlackList = null;

    /**
     * 是否已经正式开始
     */
    private boolean _officialStarted = false;

    /**
     * 是否已经定飘
     */
    private boolean _dingPiaoEnded = false;

    /**
     * 牌局队列
     */
    private final Deque<Round> _roundQ = new ArrayDeque<>();

    /**
     * 已经消费的房卡数量
     */
    private int _costRoomCard = 0;

    /**
     * 所有牌局结束
     */
    private EndOfAllRoundEnum _endOfAllRound = EndOfAllRoundEnum.UNKNOWN;

    /**
     * 解散房间会议
     */
    private DissolveRoomSession _dissolveRoomSession = null;

    /**
     * 强制结束
     */
    private boolean _forcedEnd = false;

    /**
     * 定时任务
     */
    private ITimerTask _timerTask = null;

    /**
     * 类参数构造器
     *
     * @param roomId      房间 Id
     * @param ruleSetting 规则设置
     * @param ownerId     房主用户 Id
     * @throws IllegalArgumentException if null == ruleSetting
     */
    public Room(int roomId, RuleSetting ruleSetting, int ownerId) {
        if (null == ruleSetting) {
            throw new IllegalArgumentException("ruleSetting is null");
        }

        _roomId = roomId;
        _ruleSetting = ruleSetting;
        _ownerId = ownerId;
    }

    /**
     * 获取房间 UUId
     *
     * @return 房间 UUId
     */
    public String getRoomUUId() {
        if (null == _roomUUId) {
            _roomUUId = DateTimeUtil.getDateTimeStr(this._createTime, "yyyyMMddHHmmss") + "_" + this._roomId;
        }

        return _roomUUId;
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
     * 获取游戏类型 0
     *
     * @return 游戏类型 0
     */
    public GameType0Enum getGameType0() {
        return getGameType1().getGameType0();
    }

    /**
     * 获取游戏类型 1
     *
     * @return 游戏类型 1
     */
    public GameType1Enum getGameType1() {
        return GameType1Enum.MJ_weihai_;
    }

    /**
     * 获取规则设置
     *
     * @return 规则设置
     */
    public RuleSetting getRuleSetting() {
        return _ruleSetting;
    }

    /**
     * 获取房主用户 Id
     *
     * @return 房主用户 Id
     */
    public int getOwnerId() {
        return _ownerId;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public long getCreateTime() {
        return _createTime;
    }

    /**
     * 设置创建时间
     *
     * @param val 时间戳
     */
    public void setCreateTime(long val) {
        _createTime = val;
        _roomUUId = null;
    }

    /**
     * 获取亲友圈 Id
     *
     * @return 亲友圈 Id
     */
    public int getClubId() {
        return _clubId;
    }

    /**
     * 设置亲友圈 Id
     *
     * @param val 整数值
     */
    public void setClubId(int val) {
        _clubId = val;
    }

    /**
     * 获取 ( 亲友圈内的 ) 牌桌序号
     *
     * @return (亲友圈内的) 牌桌序号
     */
    public int getTableSeqNum() {
        return _tableSeqNum;
    }

    /**
     * 设置 ( 亲友圈内的 ) 牌桌序号
     *
     * @param val 整数值
     */
    public void setTableSeqNum(int val) {
        _tableSeqNum = val;
    }

    /**
     * 根据座位索引获取玩家
     *
     * @param seatIndex 座位索引
     * @return 玩家
     */
    public Player getPlayerBySeatIndex(int seatIndex) {
        for (Player pCurr : _playerList) {
            if (null != pCurr &&
                pCurr.getSeatIndex() == seatIndex) {
                return pCurr;
            }
        }

        return null;
    }

    /**
     * 根据用户 Id 获取玩家
     *
     * @param userId 用户 Id
     * @return 玩家
     */
    public Player getPlayerByUserId(int userId) {
        for (Player pCurr : _playerList) {
            if (null != pCurr &&
                pCurr.getUserId() == userId) {
                return pCurr;
            }
        }

        return null;
    }

    /**
     * 获取玩家列表副本
     *
     * @return 玩家列表
     */
    public List<Player> getPlayerListCopy() {
        if (null == _playerListCopy) {
            _playerListCopy = List.copyOf(_playerList);
        }

        return _playerListCopy;
    }

    /**
     * 玩家就坐 ( 添加新玩家到房间 )
     *
     * @param newPlayer 新玩家
     * @return 座位索引
     */
    public int playerSitDown(Player newPlayer) {
        if (null == newPlayer) {
            return -1;
        }

        for (int i = 0; i < _ruleSetting.getMaxPlayer(); i++) {
            // 获取已有玩家
            Player oldPlayer = getPlayerBySeatIndex(i);

            if (null == oldPlayer) {
                newPlayer.setSeatIndex(i);
                _playerListCopy = null;
                _playerList.add(newPlayer);
                return i;
            } else if (oldPlayer.getUserId() == newPlayer.getUserId()) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 踢出一个玩家
     *
     * @param userId         目标用户 Id
     * @param addToBlackList 添加到黑名单
     * @return 玩家对象
     */
    public Player removePlayerByUserId(int userId, boolean addToBlackList) {
        if (userId <= 0) {
            return null;
        }

        for (int i = _playerList.size() - 1; i >= 0; i--) {
            // 获取当前玩家
            Player currPlayer = _playerList.get(i);

            if (null == currPlayer ||
                currPlayer.getUserId() != userId) {
                continue;
            }

            _playerList.remove(i);
            _playerListCopy = null;

            if (addToBlackList) {
                if (null == _userIdBlackList) {
                    _userIdBlackList = new HashSet<>();
                }

                // 将玩家加黑名单中
                _userIdBlackList.add(userId);
            }

            return currPlayer;
        }

        return null;
    }

    /**
     * 是否是已拒绝的用户 Id
     *
     * @param userId 用户 Id
     * @return true = 已经被拒绝, false = 没有被拒绝
     */
    public boolean isDeniedUserId(int userId) {
        if (userId <= 0) {
            return true;
        }

        if (null == _userIdBlackList) {
            return false;
        } else {
            return _userIdBlackList.contains(userId);
        }
    }

    /**
     * 是否已经正式开始
     *
     * @return true = 已经正式开始, false = 尚未正式开始
     */
    public boolean isOfficialStarted() {
        return _officialStarted;
    }

    /**
     * 设置已经正式开始
     *
     * @param val 布尔值
     */
    public void setOfficialStarted(boolean val) {
        _officialStarted = val;
    }

    /**
     * 设置定飘已经结束
     *
     * @param val 布尔值
     */
    public void setDingPiaoEnded(boolean val) {
        _dingPiaoEnded = val;
    }

    /**
     * 是否定飘已经结束?
     * XXX 注意: 关于何时定飘,
     * - 有些地方玩法每一局都需要重新定飘;
     * - 有些地方玩法是在第一局开局之前定飘, 之后的所有局都延用第一局的飘分;
     * - 有些地方玩法是自行选择要不要长跑;
     * <p>
     * 如果是每一局都需要重新定飘, 那么该函数
     * 就不具备参考意义了...
     *
     * @return true = 已经结束, false = 尚未结束
     */
    public boolean isDingPiaoEnded() {
        return _dingPiaoEnded;
    }

    /**
     * 开始新的牌局,
     * 会将房间用户列表复制一份赋给当前牌局
     *
     * @return 牌局
     */
    public Round beginNewRound() {
        if (EndOfAllRoundEnum.YES == _endOfAllRound) {
            // 如果确定已经结束
            return null;
        }

        // 创建新牌局
        Round newRound = new Round(_roomId, _roundQ.size(), _ruleSetting);
        newRound.setCreateTime(System.currentTimeMillis());

        for (Player currPlayer : _playerList) {
            if (null == currPlayer) {
                continue;
            }

            // 将用户复制到牌局
            Player newPlayer = new Player(currPlayer.getUserId());
            newPlayer.setUserName(currPlayer.getUserName());
            newPlayer.setHeadImg(currPlayer.getHeadImg());
            newPlayer.setSex(currPlayer.getSex());
            newPlayer.setSeatIndex(currPlayer.getSeatIndex());
            newPlayer.setRoomOwner(currPlayer.isRoomOwner());
            newPlayer.setTotalScore(currPlayer.getTotalScore());
            newPlayer.getCurrState().setPrepare(true);
            newPlayer.getCurrState().setPiaoX(currPlayer.getCurrState().getPiaoX());
            // 添加到新牌局
            newRound.addPlayer(newPlayer);

            // 清除房间用户的准备状态
            currPlayer.getCurrState().setPrepare(false);
        }

        _roundQ.offer(newRound);
        _endOfAllRound = EndOfAllRoundEnum.UNKNOWN;

        return newRound;
    }

    /**
     * 获取当前牌局
     *
     * @return 牌局
     */
    public Round getCurrRound() {
        // 获取当前牌局
        final Round currRound = _roundQ.peekLast();

        if (null == currRound ||
            currRound.isEnded()) {
            return null;
        }

        return currRound;
    }

    /**
     * 根据牌局索引获取牌局
     *
     * @param roundIndex 牌局索引
     * @return 牌局
     */
    public Round getRoundByIndex(final int roundIndex) {
        if (roundIndex < 0) {
            return null;
        }

        for (Round tempRound : _roundQ) {
            if (null != tempRound &&
                tempRound.getRoundIndex() == roundIndex) {
                return tempRound;
            }
        }

        return null;
    }

    /**
     * 获取已经结束的牌局局数
     *
     * @return 已经结束的牌局局数
     */
    public int getEndedRoundCount() {
        Round tempRound = _roundQ.peekLast();

        if (null != tempRound &&
            tempRound.isEnded()) {
            return _roundQ.size();
        } else {
            return _roundQ.size() - 1;
        }
    }

    /**
     * 获取消费房卡数量
     *
     * @return 消费房卡数量
     */
    public int getCostRoomCard() {
        return _costRoomCard;
    }

    /**
     * 设置消费房卡数量
     *
     * @param val 消费房卡数量
     */
    public void setCostRoomCard(int val) {
        _costRoomCard = val;
    }

    /**
     * 设置牌局全部结束
     *
     * @param val 枚举值
     */
    public void setEndOfAllRound(EndOfAllRoundEnum val) {
        _endOfAllRound = val;
    }

    /**
     * 全部牌局是否结束
     *
     * @return 枚举值
     */
    public EndOfAllRoundEnum getEndOfAllRound() {
        return _endOfAllRound;
    }

    /**
     * 获取解散房间会议
     *
     * @return 解散房间会议
     */
    public DissolveRoomSession getDissolveRoomSession() {
        return _dissolveRoomSession;
    }

    /**
     * 设置解散房间会议
     *
     * @param val 解散房间会议
     */
    public void setDissolveRoomSession(DissolveRoomSession val) {
        _dissolveRoomSession = val;
    }

    /**
     * 是否强制结束,
     * 一般解散操作会导致强制结束
     *
     * @return true = 强制结束, false = 没有强制结束
     */
    public boolean isForcedEnd() {
        return _forcedEnd;
    }

    /**
     * 设置强制结束
     *
     * @param val 布尔值
     */
    public void setForcedEnd(boolean val) {
        _forcedEnd = val;
    }

    /**
     * 获取定时任务
     *
     * @return 定时任务
     */
    public ITimerTask getTimerTask() {
        return _timerTask;
    }

    /**
     * 设置定时任务
     *
     * @param val 定时任务
     */
    public void setTimerTask(ITimerTask val) {
        this._timerTask = val;
    }

    /**
     * 创建 JSON 对象
     *
     * @return JSON 对象
     */
    public JSONObject toJSON() {
        JSONObject joRoot = new JSONObject(true);
        joRoot.put("roomId", this.getRoomId());
        joRoot.put("roomUUId", this.getRoomUUId());
        joRoot.put("clubId", this.getClubId());
        joRoot.put("tableSeqNum", this.getTableSeqNum());
        joRoot.put("ownerId", this.getOwnerId());
        joRoot.put("gameType0", GameType0Enum.MAHJONG.getIntVal());
        joRoot.put("gameType1", GameType1Enum.MJ_weihai_.getIntVal());
        joRoot.put("ruleMap", _ruleSetting.toJSON());
        joRoot.put("maxRound", _ruleSetting.getMaxRound());
        joRoot.put("maxPlayer", _ruleSetting.getMaxPlayer());

        if (null == this.getCurrRound()) {
            joRoot.put("currRound", -1);
        } else {
            joRoot.put("currRound", this.getCurrRound().getRoundIndex());
        }

        final JSONArray playerJsonArray = new JSONArray();
        joRoot.put("playerArray", playerJsonArray);

        this.getPlayerListCopy().forEach(
            (currPlayer) -> {
                if (null != currPlayer) {
                    playerJsonArray.add(currPlayer.toJSON());
                }
            }
        );

        return joRoot;
    }

    /**
     * 释放资源
     */
    public void free() {
        _playerList.forEach((currPlayer) -> {
            if (null != currPlayer) {
                currPlayer.free();
            }
        });

        _playerList.clear();
        _playerListCopy = null;

        if (null != _userIdBlackList) {
            _userIdBlackList.clear();
            _userIdBlackList = null;
        }

        _roundQ.forEach((currRound) -> {
            if (null != currRound) {
                currRound.free();
            }
        });

        _roundQ.clear();

        _dissolveRoomSession = null;
        _timerTask = null;
    }

    /**
     * 所有牌局结束枚举
     */
    public enum EndOfAllRoundEnum {
        /**
         * 未知
         */
        UNKNOWN(-1, "Unknown"),

        /**
         * 未结束
         */
        NO(0, "No"),

        /**
         * 已结束
         */
        YES(1, "Yes"),
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
        EndOfAllRoundEnum(int intVal, String strVal) {
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
