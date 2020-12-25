package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 麻将亮倒词条
 */
public class Wordz_MahjongLiangDao implements IWordz {
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
     * 类参数构造器
     *
     * @param userId        用户 Id
     * @param mahjongInHand 在手中的麻将
     * @param moPai         摸牌
     */
    public Wordz_MahjongLiangDao(int userId, List<MahjongTileDef> mahjongInHand, MahjongTileDef moPai) {
        _userId = userId;
        _mahjongInHand = mahjongInHand;
        _moPai = moPai;
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

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        if (null == _mahjongInHand) {
            _mahjongInHand = Collections.emptyList();
        }

        List<Integer> intObjList = _mahjongInHand.stream().map(MahjongTileDef::getIntVal).collect(Collectors.toList());

        return MJ_weihai_Protocol.MahjongLiangDaoResult.newBuilder()
            .addAllMahjongInHand(intObjList)
            .setMoPai((null == _moPai) ? -1 : _moPai.getIntVal())
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        if (null == _mahjongInHand) {
            _mahjongInHand = Collections.emptyList();
        }

        List<Integer> intObjList = _mahjongInHand.stream().map(MahjongTileDef::getIntVal).collect(Collectors.toList());

        return MJ_weihai_Protocol.MahjongLiangDaoBroadcast.newBuilder()
            .setUserId(_userId)
            .addAllMahjongInHand(intObjList)
            .setMoPai((null == _moPai) ? -1 : _moPai.getIntVal())
            .build();
    }
}
