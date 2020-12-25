package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import org.mj.bizserver.mod.game.MJ_weihai_.report.IWordz;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * 牌局
 */
public final class Round {
    /**
     * 所属房间 Id
     */
    private final int _roomId;

    /**
     * 牌局索引
     */
    private final int _roundIndex;

    /**
     * 规则设置
     */
    private final RuleSetting _ruleSetting;

    /**
     * 创建时间
     */
    private long _createTime;

    /**
     * 已开局
     */
    private boolean _began = false;

    /**
     * 已结束
     */
    private boolean _ended = false;

    /**
     * 玩家列表
     */
    private final List<Player> _playerList = new ArrayList<>();

    /**
     * 玩家列表副本
     */
    private List<Player> _playerListCopy = null;

    /**
     * 麻将牌队列 ( 等待摸牌的队列 )
     */
    private final Queue<MahjongTileDef> _mahjongTileQ = new ArrayDeque<>();

    /**
     * 亮杠腚
     */
    private final LiangGangDing _liangGangDing;

    /**
     * 已经取出的卡牌数量
     */
    private int _takeCardNum = 0;

    /**
     * 行动座位索引
     */
    private int _actSeatIndex;

    /**
     * 吃碰杠胡会话
     */
    private ChiPengGangHuSession _chiPengGangHuSession = null;

    /**
     * 回放字条列表
     */
    private final List<List<IWordz>> _playbackWordzListList = new ArrayList<>();

    /**
     * 类参数构造器
     *
     * @param roomId      所属房间 Id
     * @param roundIndex  牌局索引
     * @param ruleSetting 规则设置
     * @throws IllegalArgumentException if null == ruleSetting
     */
    Round(int roomId, int roundIndex, RuleSetting ruleSetting) {
        if (roomId <= 0 ||
            roundIndex < 0 ||
            null == ruleSetting) {
            throw new IllegalArgumentException();
        }

        _roomId = roomId;
        _roundIndex = roundIndex;
        _ruleSetting = ruleSetting;

        if (ruleSetting.isLiangGangDing()) {
            _liangGangDing = new LiangGangDing();
        } else {
            _liangGangDing = null;
        }
    }

    /**
     * 获取所属房间 Id
     *
     * @return 房间 Id
     */
    public int getRoomId() {
        return _roomId;
    }

    /**
     * 获取牌局索引
     *
     * @return 牌局索引
     */
    public int getRoundIndex() {
        return _roundIndex;
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
     * 获取牌局创建时间
     *
     * @return 牌局创建时间
     */
    public long getCreateTime() {
        return _createTime;
    }

    /**
     * 设置牌局创建时间
     *
     * @param val 牌局创建时间
     */
    public void setCreateTime(long val) {
        _createTime = val;
    }

    /**
     * 是否已经开始
     *
     * @return true = 已经开始, false = 尚未开始
     */
    public boolean isBegan() {
        return _began;
    }

    /**
     * 设置已经开始
     *
     * @param val 布尔值
     */
    public void setBegan(boolean val) {
        _began = val;
    }

    /**
     * 是否已经结束
     *
     * @return true = 已经结束, false = 尚未结束
     */
    public boolean isEnded() {
        return _ended;
    }

    /**
     * 设置已经结束
     *
     * @param val 布尔值
     */
    public void setEnded(boolean val) {
        _ended = val;
    }

    /**
     * 添加玩家
     *
     * @param newPlayer 新玩家
     */
    public void addPlayer(Player newPlayer) {
        if (null == newPlayer) {
            return;
        }

        _playerListCopy = null;
        _playerList.add(newPlayer);
    }

    /**
     * 获取玩家数量
     *
     * @return 玩家数量
     */
    public int getPlayerCount() {
        return _playerList.size();
    }

    /**
     * 获取玩家列表副本 ( 浅拷贝 )
     *
     * @return 玩家列表
     */
    public List<Player> getPlayerListCopy() {
        if (null == _playerListCopy) {
            _playerListCopy = Collections.unmodifiableList(List.copyOf(_playerList));
        }

        return _playerListCopy;
    }

    /**
     * 添加所有麻将牌
     *
     * @param tList 麻将牌列表
     */
    public void putAllMahjongTile(List<MahjongTileDef> tList) {
        _mahjongTileQ.clear();
        _mahjongTileQ.addAll(tList);

        if (null != _liangGangDing) {
            // 前两张牌放到亮杠腚里
            _liangGangDing.offer(_mahjongTileQ.poll());
            _liangGangDing.offer(_mahjongTileQ.poll());
        }
    }

    /**
     * 拿到一张麻将牌
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef takeAMahjongTile() {
        ++_takeCardNum;

        // 先从麻将队列里拿到一张牌
        MahjongTileDef t = _mahjongTileQ.poll();

        if (null == t &&
            null != _liangGangDing) {
            // 如果麻将队列里没有拿到牌,
            // 但是亮杠腚不是空,
            // 则说明还剩下 2 张牌在亮杠腚里...
            t = _liangGangDing.poll();
        }

        return t;
    }

    /**
     * 获取杠后可以摸到的第一张牌
     *
     * @return 麻将牌
     */
    public MahjongTileDef getLiangGangDingT0() {
        return _liangGangDing.getT0();
    }

    /**
     * 获取杠后可以摸到的第二张牌
     *
     * @return 麻将牌
     */
    public MahjongTileDef getLiangGangDingT1() {
        return _liangGangDing.getT1();
    }

    /**
     * 拿到亮杠腚麻将牌
     *
     * @return 麻将牌
     */
    public MahjongTileDef takeLiangGangDingT() {
        // 从亮杠腚里拿出一张牌
        MahjongTileDef t = _liangGangDing.poll();

        // 从麻将队列里拿出一张牌补到亮杠腚里
        _liangGangDing.offer(
            _mahjongTileQ.poll()
        );

        return t;
    }

    /**
     * 获取已取出的麻将牌数量
     *
     * @return 已取出的麻将牌数量
     */
    public int getTakeCardNum() {
        return _takeCardNum;
    }

    /**
     * 将已取出的麻将牌数量清零
     */
    public void resetTakeCardNum() {
        _takeCardNum = 0;
    }

    /**
     * 获取剩余卡牌 ( 麻将牌 ) 数量
     *
     * @return 剩余卡牌 ( 麻将牌 ) 数量
     */
    public int getRemainCardNum() {
        // 先看看麻将队列里的剩余牌数
        int n = _mahjongTileQ.size();

        // 如果有亮杠腚
        if (null != _liangGangDing) {
            n += _liangGangDing.count();
        }

        return n;
    }

    /**
     * 根据用户 Id 获取玩家
     *
     * @param userId 用户 Id
     * @return 玩家
     */
    public Player getPlayerByUserId(int userId) {
        if (userId <= 0) {
            return null;
        }

        for (Player currPlayer : _playerList) {
            if (null != currPlayer &&
                currPlayer.getUserId() == userId) {
                return currPlayer;
            }
        }

        return null;
    }

    /**
     * 根据座位索引获取玩家
     *
     * @param seatIndex 座位索引, 可以是 -1, 可以获得最后座位上的玩家
     * @return 玩家
     */
    public Player getPlayerBySeatIndex(int seatIndex) {
        if (_playerList.size() <= 0) {
            return null;
        }

        seatIndex = seatIndex % _playerList.size();

        if (seatIndex < 0) {
            seatIndex = _playerList.size() + seatIndex;
        }

        for (Player currPlayer : _playerList) {
            if (null != currPlayer &&
                currPlayer.getSeatIndex() == seatIndex) {
                return currPlayer;
            }
        }

        return null;
    }

    /**
     * 向前移动座位索引
     */
    public void moveToNextActSeatIndex() {
        if (++_actSeatIndex >= _playerList.size()) {
            _actSeatIndex = 0;
        }
    }

    /**
     * 根据用户 Id 调整行动座位索引
     *
     * @param userId 用户 Id
     */
    public void redirectActSeatIndexByUserId(int userId) {
        // 根据用户 Id 查找玩家
        Player foundPlayer = getPlayerByUserId(userId);

        if (null != foundPlayer) {
            this._actSeatIndex = foundPlayer.getSeatIndex();
        }
    }

    /**
     * 获取当前行动用户
     *
     * @return 用户
     */
    public Player getCurrActPlayer() {
        _actSeatIndex = Math.max(_actSeatIndex, 0);
        _actSeatIndex = Math.min(_actSeatIndex, _playerList.size());

        for (Player currPlayer : _playerList) {
            if (null != currPlayer &&
                currPlayer.getSeatIndex() == _actSeatIndex) {
                return currPlayer;
            }
        }

        return null;
    }

    /**
     * 获取吃碰杠胡会议
     *
     * @return 吃碰杠胡会议
     */
    public ChiPengGangHuSession getChiPengGangHuSession() {
        return _chiPengGangHuSession;
    }

    /**
     * 设置吃碰杠胡会议
     *
     * @param sessionObj 吃碰杠胡会议
     */
    public void setChiPengGangHuSession(ChiPengGangHuSession sessionObj) {
        _chiPengGangHuSession = sessionObj;
    }

    /**
     * 添加回放字条列表
     *
     * @param wordzList 回放字条列表
     */
    public void addPlaybackWordzList(List<IWordz> wordzList) {
        if (null != wordzList &&
            wordzList.size() > 0) {
            _playbackWordzListList.add(wordzList);
        }
    }

    /**
     * 获取回放字条列表
     *
     * @return 回放字条列表
     */
    public List<List<IWordz>> getPlaybackWordzListList() {
        return _playbackWordzListList;
    }

    /**
     * 清理回放字条列表
     */
    public void clearPlaybackWordzListList() {
        _playbackWordzListList.forEach((wL) -> {
            if (null != wL) {
                wL.clear();
            }
        });

        _playbackWordzListList.clear();
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
        _mahjongTileQ.clear();
        _chiPengGangHuSession = null;
        _playbackWordzListList.clear();
    }
}
