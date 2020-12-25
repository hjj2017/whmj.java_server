package org.mj.bizserver.mod.game.MJ_weihai_.report;

import com.google.protobuf.GeneratedMessageV3;
import org.mj.bizserver.allmsg.MJ_weihai_Protocol;

/**
 * 定飘词条
 */
public class Wordz_DingPiao implements IWordz {
    /**
     * 用户 Id
     */
    private final int _userId;

    /**
     * 飘数值
     */
    private final int _piaoX;

    /**
     * 类参数构造器
     *
     * @param userId 用户 Id
     * @param piaoX  飘几
     */
    public Wordz_DingPiao(int userId, int piaoX) {
        _userId = userId;
        _piaoX = piaoX;
    }

    @Override
    public int getUserId() {
        return _userId;
    }

    /**
     * 获取飘几
     *
     * @return 0 = 不飘, 1 = 飘_1, 2 = 飘_2, 3 = 飘_3, 4 = 飘_4
     */
    public int getPiaoX() {
        return _piaoX;
    }

    @Override
    public GeneratedMessageV3 buildResultMsg() {
        return MJ_weihai_Protocol.DingPiaoResult.newBuilder()
            .setPiaoX(_piaoX)
            .setOk(true)
            .build();
    }

    @Override
    public GeneratedMessageV3 buildBroadcastMsg() {
        return MJ_weihai_Protocol.DingPiaoBroadcast.newBuilder()
            .setUserId(_userId)
            .setPiaoX(_piaoX)
            .setOk(true)
            .build();
    }
}
