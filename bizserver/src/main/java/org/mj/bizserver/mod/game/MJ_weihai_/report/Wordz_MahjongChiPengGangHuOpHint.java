package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.ChiChoiceQuestion;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.LiangFengChoiceQuestion;

/**
 * 麻将 "吃碰杠胡" 操作提示词条
 */
public class Wordz_MahjongChiPengGangHuOpHint implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 吃牌提示
     */
    private final boolean _opHintChi;

    /**
     * 吃牌选择题
     */
    private final ChiChoiceQuestion _chiChoiceQuestion;

    /**
     * 碰牌提示
     */
    private final boolean _opHintPeng;

    /**
     * 杠牌提示
     */
    private final boolean _opHintGang;

    /**
     * 胡牌提示
     */
    private final boolean _opHintHu;

    /**
     * 亮风提示
     */
    private boolean _opHintLiangFeng = false;

    /**
     * 亮风选择题
     */
    private LiangFengChoiceQuestion _liangFengChoiceQuestion = null;

    /**
     * 补风提示
     */
    private boolean _opHintBuFeng = false;

    /**
     * 类参数构造器
     *
     * @param opHintChi         吃牌提示
     * @param chiChoiceQuestion 吃牌选择题
     * @param opHintPeng        碰牌提示
     * @param opHintGang        杠牌提示, 包括明杠、暗杠、补杠都用这个参数
     * @param opHintHu          胡牌提示, 包括胡和自摸都用这个参数
     */
    public Wordz_MahjongChiPengGangHuOpHint(
        int userId, boolean opHintChi, ChiChoiceQuestion chiChoiceQuestion, boolean opHintPeng, boolean opHintGang, boolean opHintHu) {
        _userId = userId;
        _opHintChi = opHintChi;
        _chiChoiceQuestion = chiChoiceQuestion;
        _opHintPeng = opHintPeng;
        _opHintGang = opHintGang;
        _opHintHu = opHintHu;
    }

    /**
     * 类参数构造器
     *
     * @param userId     用户 Id
     * @param opHintGang 杠牌提示, 包括明杠、暗杠、补杠都用这个参数
     * @param opHintHu   胡牌提示, 包括胡和自摸都用这个参数
     */
    public Wordz_MahjongChiPengGangHuOpHint(int userId, boolean opHintGang, boolean opHintHu) {
        _userId = userId;
        _opHintChi = false;
        _chiChoiceQuestion = null;
        _opHintPeng = false;
        _opHintGang = opHintGang;
        _opHintHu = opHintHu;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 是否提示吃牌
     *
     * @return true = 提示, false = 不提示
     */
    public boolean isOpHintChi() {
        return _opHintChi;
    }

    /**
     * 获取吃牌选择题
     *
     * @return 吃牌选择题
     */
    public ChiChoiceQuestion getChiChoiceQuestion() {
        return _chiChoiceQuestion;
    }

    /**
     * 是否提示碰牌
     *
     * @return true = 提示, false = 不提示
     */
    public boolean isOpHintPeng() {
        return _opHintPeng;
    }

    /**
     * 是否提示杠牌
     *
     * @return true = 提示, false = 不提示
     */
    public boolean isOpHintGang() {
        return _opHintGang;
    }

    /**
     * 是否提示胡牌
     *
     * @return true = 提示, false = 不提示
     */
    public boolean isOpHintHu() {
        return _opHintHu;
    }

    /**
     * 是否提示亮风
     *
     * @return true = 提示, false = 不提示
     */
    public boolean isOpHintLiangFeng() {
        return _opHintLiangFeng;
    }

    /**
     * 设置亮风提示
     *
     * @param val            布尔值
     * @param choiceQuestion 亮风选择题
     * @return this 指针
     */
    public Wordz_MahjongChiPengGangHuOpHint putOpHintLiangFeng(boolean val, LiangFengChoiceQuestion choiceQuestion) {
        _opHintLiangFeng = val;
        _liangFengChoiceQuestion = choiceQuestion;
        return this;
    }

    /**
     * 是否提示亮风
     *
     * @return true = 提示, false = 不提示
     */
    public boolean isOpHintBuFeng() {
        return _opHintBuFeng;
    }

    /**
     * 设置亮风提示
     *
     * @param val 布尔值
     * @return this 指针
     */
    public Wordz_MahjongChiPengGangHuOpHint putOpHintBuFeng(boolean val) {
        _opHintBuFeng = val;
        return this;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        MJ_weihai_Protocol.MahjongChiPengGangHuOpHintResult.Builder
            b0 = MJ_weihai_Protocol.MahjongChiPengGangHuOpHintResult.newBuilder()
            .setOpHintChi(_opHintChi)
            .setOpHintPeng(_opHintPeng)
            .setOpHintGang(_opHintGang)
            .setOpHintHu(_opHintHu)
            .setOpHintLiangFeng(_opHintLiangFeng)
            .setOpHintBuFeng(_opHintBuFeng);

        if (null != _chiChoiceQuestion) {
            // 构建吃牌选择题
            MJ_weihai_Protocol.ChiChoiceQuestion.Builder b1 = MJ_weihai_Protocol.ChiChoiceQuestion.newBuilder()
                .setChiT(_chiChoiceQuestion.getChiTIntVal())
                .setDisplayOptionA(_chiChoiceQuestion.isDisplayOptionA())
                .setDisplayOptionB(_chiChoiceQuestion.isDisplayOptionB())
                .setDisplayOptionC(_chiChoiceQuestion.isDisplayOptionC());
            b0.setChiChoiceQuestion(b1);
        }

        if (null != _liangFengChoiceQuestion) {
            // 构建亮风选择题
            MJ_weihai_Protocol.LiangFengChoiceQuestion.Builder b2 = MJ_weihai_Protocol.LiangFengChoiceQuestion.newBuilder()
                .setLuanMao(_liangFengChoiceQuestion.isLuanMao())
                .setDisplayOptionDongFeng(_liangFengChoiceQuestion.isDisplayOptionDongFeng())
                .setDisplayOptionNanFeng(_liangFengChoiceQuestion.isDisplayOptionNanFeng())
                .setDisplayOptionXiFeng(_liangFengChoiceQuestion.isDisplayOptionXiFeng())
                .setDisplayOptionBeiFeng(_liangFengChoiceQuestion.isDisplayOptionBeiFeng())
                .setDisplayOptionHongZhong(_liangFengChoiceQuestion.isDisplayOptionHongZhong())
                .setDisplayOptionFaCai(_liangFengChoiceQuestion.isDisplayOptionFaCai())
                .setDisplayOptionBaiBan(_liangFengChoiceQuestion.isDisplayOptionBaiBan());
            b0.setLiangFengChoiceQuestion(b2);
        }

        return b0.build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        // 吃碰杠胡操作提示是不能广播给其他人的
        return null;
    }
}
