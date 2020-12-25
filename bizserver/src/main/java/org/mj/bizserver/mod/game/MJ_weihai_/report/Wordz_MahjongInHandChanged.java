package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 手牌变化词条
 */
public class Wordz_MahjongInHandChanged implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 在手中的麻将
     */
    private List<MahjongTileDef> _mahjongInHand;

    /**
     * 摸牌
     */
    private final MahjongTileDef _moPai;

    /**
     * 是否遮掩
     */
    private final boolean _mask;

    /**
     * 类参数构造器
     *
     * @param userId        用户 Id
     * @param mahjongInHand 在手中的麻将
     * @param moPai         摸牌
     */
    public Wordz_MahjongInHandChanged(int userId, List<MahjongTileDef> mahjongInHand, MahjongTileDef moPai) {
        this(userId, mahjongInHand, moPai, false);
    }

    /**
     * 类参数构造器
     *
     * @param userId        用户 Id
     * @param mahjongInHand 在手中的麻将
     * @param moPai         摸牌
     * @param mask          是否遮掩
     */
    public Wordz_MahjongInHandChanged(int userId, List<MahjongTileDef> mahjongInHand, MahjongTileDef moPai, boolean mask) {
        _userId = userId;
        _mahjongInHand = mahjongInHand;
        _moPai = moPai;
        _mask = mask;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取在手中的麻将
     *
     * @return 在手中的麻将
     */
    public List<MahjongTileDef> getMahjongInHand() {
        return _mahjongInHand;
    }

    /**
     * 获取摸牌
     *
     * @return 麻将牌
     */
    public MahjongTileDef getMoPai() {
        return _moPai;
    }

    /**
     * 是否遮掩
     *
     * @return true = 遮掩, fase = 不遮掩
     */
    public boolean isMask() {
        return _mask;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        if (null == _mahjongInHand) {
            _mahjongInHand = Collections.emptyList();
        }

        List<Integer> intObjList = _mahjongInHand.stream().map(MahjongTileDef::getIntVal).collect(Collectors.toList());

        return MJ_weihai_Protocol.MahjongInHandChangedResult.newBuilder()
            .addAllMahjongInHand(intObjList)
            .setMoPai((null == _moPai) ? -1 : _moPai.getIntVal())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        if (null == _mahjongInHand) {
            _mahjongInHand = Collections.emptyList();
        }

        List<Integer> intObjList = _mahjongInHand.stream()
            .map((t) -> _mask ? MahjongTileDef.MASK_VAL : t.getIntVal())
            .collect(Collectors.toList());

        // 获取摸牌整数值
        int moPaiIntVal = -1;

        if (null != _moPai) {
            moPaiIntVal = _mask ? MahjongTileDef.MASK_VAL : _moPai.getIntVal();
        }

        return MJ_weihai_Protocol.MahjongInHandChangedBroadcast.newBuilder()
            .setUserId(_userId)
            .addAllMahjongInHand(intObjList)
            .setMoPai(moPaiIntVal)
            .build();
    }

    @Override
    public JSONObject buildJSONObj() {
        List<Integer> intObjList = _mahjongInHand.stream().map(MahjongTileDef::getIntVal).collect(Collectors.toList());

        final JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("clazzName", this.getClass().getSimpleName());
        jsonObj.put("userId", _userId);
        jsonObj.put("mahjongInHand", intObjList);
        jsonObj.put("moPai", (null != _moPai) ? _moPai.getIntVal() : -1);

        return jsonObj;
    }

    @Override
    public Wordz_MahjongInHandChanged createMaskCopy() {
        return new Wordz_MahjongInHandChanged(_userId, _mahjongInHand, _moPai,true);
    }
}
