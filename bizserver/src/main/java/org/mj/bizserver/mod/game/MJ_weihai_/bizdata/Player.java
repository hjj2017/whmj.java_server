package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 玩家
 */
public final class Player {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 用户名称
     */
    private String _userName;

    /**
     * 头像
     */
    private String _headImg;

    /**
     * 性别
     */
    private int _sex;

    /**
     * 地理位置
     */
    private final GeoLocation _geoLocation = new GeoLocation();

    /**
     * 座位索引
     */
    private int _seatIndex;

    /**
     * 当前分数
     */
    private int _currScore;

    /**
     * 总分数
     */
    private int _totalScore;

    /**
     * 大赢家
     */
    private boolean _bigWinner = false;

    /**
     * 房主
     */
    private boolean _roomOwner;

    /**
     * 状态表
     */
    private final StateTable _currState = new StateTable();

    /**
     * 亮风
     * XXX 注意: 这是威海麻将特色玩法
     */
    private final MahjongLiangFeng _mahjongLiangFeng = new MahjongLiangFeng();

    /**
     * 麻将吃碰杠列表
     */
    private final List<MahjongChiPengGang> _mahjongChiPengGangList = new ArrayList<>();

    /**
     * 麻将吃碰杠列表副本
     */
    private List<MahjongChiPengGang> _mahjongChiPengGangListCopy = null;

    /**
     * 麻将手牌列表
     */
    private final List<MahjongTileDef> _mahjongInHand = new ArrayList<>();

    /**
     * 麻将手牌列表副本
     */
    private List<MahjongTileDef> _mahjongInHandCopy = null;

    /**
     * 已打出的麻将牌
     */
    private final Deque<MahjongTileDef> _mahjongOutput = new ArrayDeque<>();

    /**
     * 摸到的牌
     */
    private MahjongTileDef _moPai;

    /**
     * 结算结果
     */
    private final SettlementResult _settlementResult = new SettlementResult();

    /**
     * 类参数构造器
     *
     * @param userId 用户 Id
     */
    public Player(int userId) {
        _userId = userId;
    }

    /**
     * 获取用户 Id
     *
     * @return 用户 Id
     */
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取用户名称
     *
     * @return 用户名称
     */
    public String getUserName() {
        return Objects.requireNonNullElse(_userName, "");
    }

    /**
     * 设置用户名称
     *
     * @param val 字符串值
     */
    public void setUserName(String val) {
        _userName = val;
    }

    /**
     * 获取头像
     *
     * @return 头像
     */
    public String getHeadImg() {
        return Objects.requireNonNullElse(_headImg, "");
    }

    /**
     * 设置头像
     *
     * @param val 字符串值
     */
    public void setHeadImg(String val) {
        _headImg = val;
    }

    /**
     * 获取性别
     *
     * @return -1 = 未知, 0 = 女, 1 = 男
     */
    public int getSex() {
        return _sex;
    }

    /**
     * 设置性别
     *
     * @param val 整数值
     */
    public void setSex(int val) {
        _sex = val;
    }

    /**
     * 获取地理位置
     *
     * @return 地理位置
     */
    public GeoLocation getGeoLocation() {
        return _geoLocation;
    }

    /**
     * 获取客户端 IP 地址
     *
     * @return 客户端 IP 地址
     */
    public String getClientIpAddr() {
        return Objects.requireNonNullElse(_geoLocation.getClientIpAddr(), "");
    }

    /**
     * 设置客户端 IP 地址
     *
     * @param val 字符串值
     */
    public void setClientIpAddr(String val) {
        _geoLocation.setClientIpAddr(val);
    }

    /**
     * 获取座位索引
     *
     * @return 座位索引
     */
    public int getSeatIndex() {
        return _seatIndex;
    }

    /**
     * 设置座位索引
     *
     * @param val 整数值
     */
    public void setSeatIndex(int val) {
        _seatIndex = val;
    }

    /**
     * 获取当前分数
     *
     * @return 当前分数
     */
    public int getCurrScore() {
        return _currScore;
    }

    /**
     * 设置当前分数
     *
     * @param val 整数值
     */
    public void setCurrScore(int val) {
        _currScore = val;
    }

    /**
     * 获取总分
     *
     * @return 总分
     */
    public int getTotalScore() {
        return _totalScore;
    }

    /**
     * 设置总分
     *
     * @param val 整数值
     */
    public void setTotalScore(int val) {
        _totalScore = val;
    }

    /**
     * 是否为大赢家
     *
     * @return true = 是大赢家, false = 不是大赢家
     */
    public boolean isBigWinner() {
        return _bigWinner;
    }

    /**
     * 设置大赢家
     *
     * @param val 布尔值
     */
    public void setBigWinner(boolean val) {
        _bigWinner = val;
    }

    /**
     * 是否是房主
     *
     * @return true = 是房主, false = 不是房主
     */
    public boolean isRoomOwner() {
        return _roomOwner;
    }

    /**
     * 设置房主标志
     *
     * @param val 布尔值
     */
    public void setRoomOwner(boolean val) {
        _roomOwner = val;
    }

    /**
     * 获取当前状态
     *
     * @return 当前状态
     */
    public StateTable getCurrState() {
        return _currState;
    }

    /**
     * 获取麻将亮风,
     * XXX 注意: 亮风是威海麻将特有的玩法
     *
     * @return 麻将亮风
     */
    public MahjongLiangFeng getMahjongLiangFeng() {
        return _mahjongLiangFeng;
    }

    /**
     * 获取麻将吃碰杠 ( 包括明杠、暗杠、补杠 ) 列表副本,
     * XXX 注意: 这个列表副本是只读的!
     *
     * @return 麻将吃碰杠列表
     */
    public List<MahjongChiPengGang> getMahjongChiPengGangListCopy() {
        if (null == _mahjongChiPengGangListCopy) {
            _mahjongChiPengGangListCopy = Collections.unmodifiableList(List.copyOf(_mahjongChiPengGangList));
        }

        return _mahjongChiPengGangListCopy;
    }

    /**
     * 添加麻将吃牌
     *
     * @param tChi       吃的是哪一张麻将牌, XXX 注意: tX 是 t0、t1、t2 中的其中一个
     * @param t0         第一张牌
     * @param t1         第二张牌
     * @param t2         第三张牌
     * @param fromUserId 从谁那里吃的
     */
    public void addMahjongChi(
        MahjongTileDef tChi, MahjongTileDef t0, MahjongTileDef t1, MahjongTileDef t2, int fromUserId) {
        if (null == tChi ||
            null == t0 ||
            null == t1 ||
            null == t2) {
            return;
        }

        if (tChi != t0 &&
            tChi != t1 &&
            tChi != t2) {
            return;
        }

        _mahjongChiPengGangListCopy = null;
        _mahjongChiPengGangList.add(new MahjongChiPengGang(
            MahjongChiPengGang.KindDef.CHI, tChi, t0, t1, t2, fromUserId
        ));
    }

    /**
     * 添加麻将碰牌
     *
     * @param tPeng      碰的是哪一张牌
     * @param fromUserId 从谁那里碰的
     */
    public void addMahjongPeng(MahjongTileDef tPeng, int fromUserId) {
        if (null == tPeng) {
            return;
        }

        _mahjongChiPengGangListCopy = null;
        _mahjongChiPengGangList.add(new MahjongChiPengGang(
            MahjongChiPengGang.KindDef.PENG, tPeng, null, null, null, fromUserId
        ));
    }

    /**
     * 添加麻将明杠
     *
     * @param tMingGang  杠的是哪一张牌
     * @param fromUserId 从谁那里杠得的
     */
    public void addMahjongMingGang(MahjongTileDef tMingGang, int fromUserId) {
        if (null == tMingGang) {
            return;
        }

        _mahjongChiPengGangListCopy = null;
        _mahjongChiPengGangList.add(new MahjongChiPengGang(
            MahjongChiPengGang.KindDef.MING_GANG, tMingGang, null, null, null, fromUserId
        ));
    }

    /**
     * 添加麻将暗杠
     *
     * @param tAnGang 杠的是哪一张牌
     */
    public void addMahjongAnGang(MahjongTileDef tAnGang) {
        if (null == tAnGang) {
            return;
        }

        _mahjongChiPengGangListCopy = null;
        _mahjongChiPengGangList.add(new MahjongChiPengGang(
            MahjongChiPengGang.KindDef.AN_GANG, tAnGang, null, null, null, -1
        ));
    }

    /**
     * 是否可以补杠
     *
     * @param tBuGang 要补杠的麻将牌
     * @return true = 可以补杠, false = 不能补杠
     */
    public boolean canBuGang(MahjongTileDef tBuGang) {
        if (null == tBuGang) {
            return false;
        }

        for (MahjongChiPengGang mahjongChiPengGang : _mahjongChiPengGangList) {
            if (null == mahjongChiPengGang) {
                continue;
            }

            if (mahjongChiPengGang.getKind() == MahjongChiPengGang.KindDef.PENG &&
                mahjongChiPengGang.getTX() == tBuGang) {
                return true;
            }
        }

        return false;
    }

    /**
     * 升级麻将碰牌为补杠
     *
     * @param tBuGang 要补杠的麻将牌
     */
    public void upgradePengToBuGang(MahjongTileDef tBuGang) {
        if (null == tBuGang) {
            return;
        }

        for (MahjongChiPengGang mahjongChiPengGang : _mahjongChiPengGangList) {
            if (null == mahjongChiPengGang) {
                continue;
            }

            if (mahjongChiPengGang.getKind() == MahjongChiPengGang.KindDef.PENG &&
                mahjongChiPengGang.getTX() == tBuGang) {
                _mahjongChiPengGangListCopy = null;
                mahjongChiPengGang.upgradePengToBuGang();
                return;
            }
        }
    }

    /**
     * 获取手中的麻将列表副本,
     * XXX 注意: 这个列表副本是只读的!
     *
     * @return 麻将列表
     */
    public List<MahjongTileDef> getMahjongInHandCopy() {
        if (null == _mahjongInHandCopy) {
            _mahjongInHandCopy = Collections.unmodifiableList(List.copyOf(_mahjongInHand));
        }

        return _mahjongInHandCopy;
    }

    /**
     * 麻将手牌中是否含有指定的麻将牌
     *
     * @param t 麻将牌
     * @return true = 有, false = 没有
     */
    public boolean hasAMahjongTileInHand(MahjongTileDef t) {
        if (null == t) {
            return false;
        } else {
            return _mahjongInHand.contains(t);
        }
    }

    /**
     * 添加一张麻将牌到手里
     *
     * @param t 麻将牌
     */
    public void addAMahjongTileInHand(MahjongTileDef t) {
        if (null == t) {
            return;
        }

        _mahjongInHandCopy = null;
        _mahjongInHand.add(t);
        _mahjongInHand.sort(Comparator.comparingInt(MahjongTileDef::getIntVal));
    }

    /**
     * 从手里删除一张牌麻将牌
     *
     * @param t 麻将牌
     */
    public void removeAMahjongTileInHand(MahjongTileDef t) {
        if (null == t) {
            return;
        }

        _mahjongInHandCopy = null;
        _mahjongInHand.remove(t);
        _mahjongInHand.sort(Comparator.comparingInt(MahjongTileDef::getIntVal));
    }

    /**
     * 获得在手里的最右边的一张麻将牌
     *
     * @return 麻将牌
     */
    public MahjongTileDef getTheRightmostMahjongTileInHand() {
        if (_mahjongInHand.size() <= 0) {
            return null;
        } else {
            return _mahjongInHand.get(_mahjongInHand.size() - 1);
        }
    }

    /**
     * 获取手中的麻将牌整数列表
     *
     * @return 整数列表
     */
    public List<Integer> getMahjongInHandIntValList() {
        return _mahjongInHand.stream().map(MahjongTileDef::getIntVal).collect(Collectors.toList());
    }

    /**
     * 获取已经打出的牌的队列
     *
     * @return 麻将队列
     */
    public Deque<MahjongTileDef> getMahjongOutput() {
        return _mahjongOutput;
    }

    /**
     * 获取已经打出的的牌的整数列表
     *
     * @return 整数列表
     */
    public List<Integer> getMahjongOutputIntValList() {
        return _mahjongOutput.stream().map(MahjongTileDef::getIntVal).collect(Collectors.toList());
    }

    /**
     * 获取摸牌
     *
     * @return 麻将牌定义
     */
    public MahjongTileDef getMoPai() {
        return _moPai;
    }

    /**
     * 获取摸牌整数值
     *
     * @return 整数值
     */
    public int getMoPaiIntVal() {
        return null == _moPai ? -1 : _moPai.getIntVal();
    }

    /**
     * 设置摸牌
     *
     * @param val 麻将牌定义
     */
    public void setMoPai(MahjongTileDef val) {
        _moPai = val;
    }

    /**
     * 获取结算结果
     *
     * @return 结算结果
     */
    public SettlementResult getSettlementResult() {
        return _settlementResult;
    }

    /**
     * 获取 JSON 对象
     *
     * @return JSON 对象
     */
    public JSONObject toJSON() {
        JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("userId", this.getUserId());
        jsonObj.put("userName", this.getUserName());
        jsonObj.put("headImg", this.getHeadImg());
        jsonObj.put("sex", this.getSex());
        jsonObj.put("seatIndex", this.getSeatIndex());

        return jsonObj;
    }

    /**
     * 释放资源
     */
    public void free() {
        _mahjongLiangFeng.free();
        _mahjongChiPengGangList.clear();
        _mahjongChiPengGangListCopy = null;
        _mahjongInHand.clear();
        _mahjongInHandCopy = null;
        _mahjongOutput.clear();
        _moPai = null;
        _settlementResult.free();
    }
}
