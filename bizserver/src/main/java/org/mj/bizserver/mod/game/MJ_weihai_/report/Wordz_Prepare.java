package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;

/**
 * 准备词条
 */
public class Wordz_Prepare implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 是否准备好, 0 = 取消准备, 1 = 准备好
     */
    private final int _yes;

    /**
     * 是否全部准备好
     */
    private boolean _allReady = false;

    /**
     * 类参数构造器
     *
     * @param userId 用户 Id
     * @param yes     是否准备好, 0 = 取消准备, 1 = 准备好
     */
    public Wordz_Prepare(int userId, int yes) {
        this._userId = userId;
        this._yes = yes;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 是否准备好
     *
     * @return 0 = 取消准备, 1 = 准备好
     */
    public int getYes() {
        return _yes;
    }

    /**
     * 是否全部准备好
     *
     * @return true = 全部准备好
     */
    public boolean isAllReady() {
        return _allReady;
    }

    /**
     * 设置全部准备好
     *
     * @param val 布尔值
     */
    public void setAllReady(boolean val) {
        _allReady = val;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.PrepareResult.newBuilder()
            .setYes(_yes)
            .setOk(true)
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.PrepareBroadcast.newBuilder()
            .setUserId(_userId)
            .setYes(_yes)
            .setAllReady(_allReady)
            .build();
    }
}
