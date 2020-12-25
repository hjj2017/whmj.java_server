package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;

/**
 * 选飘提示
 */
public class Wordz_SelectPiaoHint implements IWordz {
    /**
     * 不飘
     */
    private boolean _buPiao;

    /**
     * 飘_1
     */
    private boolean _piao1;

    /**
     * 飘_2
     */
    private boolean _piao2;

    /**
     * 飘_3
     */
    private boolean _piao3;

    /**
     * 飘_4
     */
    private boolean _piao4;

    /**
     * 类参数构造器
     *
     * @param buPiao 不飘
     * @param piao1  飘_1
     * @param piao2  飘_2
     * @param piao3  飘_3
     * @param piao4  飘_4
     */
    public Wordz_SelectPiaoHint(boolean buPiao, boolean piao1, boolean piao2, boolean piao3, boolean piao4) {
        _buPiao = buPiao;
        _piao1 = piao1;
        _piao2 = piao2;
        _piao3 = piao3;
        _piao4 = piao4;
    }

    /**
     * 是否可以选择不飘
     *
     * @return true = 可以选择, false = 不能选择
     */
    public boolean isBuPiao() {
        return _buPiao;
    }

    /**
     * 是否可以选择飘_1
     *
     * @return true = 可以选择, false = 不能选择
     */
    public boolean isPiao1() {
        return _piao1;
    }

    /**
     * 是否可以选择飘_2
     *
     * @return true = 可以选择, false = 不能选择
     */
    public boolean isPiao2() {
        return _piao2;
    }

    /**
     * 是否可以选择飘_3
     *
     * @return true = 可以选择, false = 不能选择
     */
    public boolean isPiao3() {
        return _piao3;
    }

    /**
     * 是否可以选择飘_4
     *
     * @return true = 可以选择, false = 不能选择
     */
    public boolean isPiao4() {
        return _piao4;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return null;
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.SelectPiaoHintBroadcast.newBuilder()
            .setBuPiao(_buPiao)
            .setPiao1(_piao1)
            .setPiao2(_piao2)
            .setPiao3(_piao3)
            .setPiao4(_piao4)
            .build();
    }
}
