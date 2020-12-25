package org.mj.bizserver.mod.game.MJ_weihai_.bizdata;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 吃碰杠胡会议测试
 */
public class ChiPengGangHuSessionTest {
    /**
     * 构建测试房间
     */
    static private void buildTestRoom() {
        // 创建规则字典
        Map<Integer, Integer> ruleMap = new HashMap<>();
        ruleMap.put(RuleSetting.KEY_MAX_PLAYER, 4);
        ruleMap.put(RuleSetting.KEY_MAX_ROUND, 8);

        // 创建测试房间
        Room testRoom = new Room(123456, new RuleSetting(ruleMap), 1001);

        for (int i = 1001; i <= 1004; i++) {
            // 创建测试玩家
            Player testPlayer = new Player(i);
            testPlayer.setUserName("Test_" + i);
            // 玩家就坐
            testRoom.playerSitDown(testPlayer);
        }

        // 添加到房间分组
        RoomGroup.add(testRoom);
        // 开局
        testRoom.beginNewRound();
    }

    /**
     * 测试非一炮多响
     */
    @Test
    public void test_notYiPaoDuoXiang() {
        // 构建测试房间
        buildTestRoom();

        ChiPengGangHuSession sessionObj = new ChiPengGangHuSession(MahjongTileDef._1_BING, 1001);
        sessionObj.addUserIdCanHu(1003); // 注意这里的顺序, 非一炮多响必须按照座位顺序来
        sessionObj.addUserIdCanHu(1002);
        sessionObj.putUserIdCanChi(1002, new ChiChoiceQuestion(MahjongTileDef._1_BING, true, false, false));
        sessionObj.putUserIdCanPeng(1004);
        sessionObj.putYiPaoDuoXiang(false);

        // 获取当前对话
        ChiPengGangHuSession.Dialog currD = sessionObj.getCurrDialog();

        // 1001 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1001));
        Assert.assertFalse(currD.isUserIdCanPeng(1001));
        Assert.assertFalse(currD.isUserIdCanMingGang(1001));
        Assert.assertFalse(currD.isUserIdCanAnGang(1001));
        Assert.assertFalse(currD.isUserIdCanBuGang(1001));
        Assert.assertFalse(currD.isUserIdCanHu(1001));

        // 1002 只能胡牌, 吃碰杠都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1002));
        Assert.assertFalse(currD.isUserIdCanPeng(1002));
        Assert.assertFalse(currD.isUserIdCanMingGang(1002));
        Assert.assertFalse(currD.isUserIdCanAnGang(1002));
        Assert.assertFalse(currD.isUserIdCanBuGang(1002));
        Assert.assertTrue(currD.isUserIdCanHu(1002));

        // 1003 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1003));
        Assert.assertFalse(currD.isUserIdCanPeng(1003));
        Assert.assertFalse(currD.isUserIdCanMingGang(1003));
        Assert.assertFalse(currD.isUserIdCanAnGang(1003));
        Assert.assertFalse(currD.isUserIdCanBuGang(1003));
        Assert.assertFalse(currD.isUserIdCanHu(1003));

        // 1004 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1004));
        Assert.assertFalse(currD.isUserIdCanPeng(1004));
        Assert.assertFalse(currD.isUserIdCanMingGang(1004));
        Assert.assertFalse(currD.isUserIdCanAnGang(1004));
        Assert.assertFalse(currD.isUserIdCanBuGang(1004));
        Assert.assertFalse(currD.isUserIdCanHu(1004));

        // 移到下一对话
        sessionObj.moveToNextDialog();
        currD = sessionObj.getCurrDialog();

///////////////////////////////////////////////////////////////////////

        // 1001 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1001));
        Assert.assertFalse(currD.isUserIdCanPeng(1001));
        Assert.assertFalse(currD.isUserIdCanMingGang(1001));
        Assert.assertFalse(currD.isUserIdCanAnGang(1001));
        Assert.assertFalse(currD.isUserIdCanBuGang(1001));
        Assert.assertFalse(currD.isUserIdCanHu(1001));

        // 1002 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1002));
        Assert.assertFalse(currD.isUserIdCanPeng(1002));
        Assert.assertFalse(currD.isUserIdCanMingGang(1002));
        Assert.assertFalse(currD.isUserIdCanAnGang(1002));
        Assert.assertFalse(currD.isUserIdCanBuGang(1002));
        Assert.assertFalse(currD.isUserIdCanHu(1002));

        // 1003 只能胡牌
        Assert.assertFalse(currD.isUserIdCanChi(1003));
        Assert.assertFalse(currD.isUserIdCanPeng(1003));
        Assert.assertFalse(currD.isUserIdCanMingGang(1003));
        Assert.assertFalse(currD.isUserIdCanAnGang(1003));
        Assert.assertFalse(currD.isUserIdCanBuGang(1003));
        Assert.assertTrue(currD.isUserIdCanHu(1003));

        // 1004 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1004));
        Assert.assertFalse(currD.isUserIdCanPeng(1004));
        Assert.assertFalse(currD.isUserIdCanMingGang(1004));
        Assert.assertFalse(currD.isUserIdCanAnGang(1004));
        Assert.assertFalse(currD.isUserIdCanBuGang(1004));
        Assert.assertFalse(currD.isUserIdCanHu(1004));

        // 移到下一对话
        sessionObj.moveToNextDialog();
        currD = sessionObj.getCurrDialog();

///////////////////////////////////////////////////////////////////////

        // 1001 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1001));
        Assert.assertFalse(currD.isUserIdCanPeng(1001));
        Assert.assertFalse(currD.isUserIdCanMingGang(1001));
        Assert.assertFalse(currD.isUserIdCanAnGang(1001));
        Assert.assertFalse(currD.isUserIdCanBuGang(1001));
        Assert.assertFalse(currD.isUserIdCanHu(1001));

        // 1002 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1002));
        Assert.assertFalse(currD.isUserIdCanPeng(1002));
        Assert.assertFalse(currD.isUserIdCanMingGang(1002));
        Assert.assertFalse(currD.isUserIdCanAnGang(1002));
        Assert.assertFalse(currD.isUserIdCanBuGang(1002));
        Assert.assertFalse(currD.isUserIdCanHu(1002));

        // 1003 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1003));
        Assert.assertFalse(currD.isUserIdCanPeng(1003));
        Assert.assertFalse(currD.isUserIdCanMingGang(1003));
        Assert.assertFalse(currD.isUserIdCanAnGang(1003));
        Assert.assertFalse(currD.isUserIdCanBuGang(1003));
        Assert.assertFalse(currD.isUserIdCanHu(1003));

        // 1004 只能碰牌
        Assert.assertFalse(currD.isUserIdCanChi(1004));
        Assert.assertTrue(currD.isUserIdCanPeng(1004));
        Assert.assertFalse(currD.isUserIdCanMingGang(1004));
        Assert.assertFalse(currD.isUserIdCanAnGang(1004));
        Assert.assertFalse(currD.isUserIdCanBuGang(1004));
        Assert.assertFalse(currD.isUserIdCanHu(1004));

        // 移到下一对话
        sessionObj.moveToNextDialog();
        currD = sessionObj.getCurrDialog();

///////////////////////////////////////////////////////////////////////

        // 1002 只能吃
        Assert.assertTrue(currD.isUserIdCanChi(1002));
        Assert.assertFalse(currD.isUserIdCanPeng(1002));
        Assert.assertFalse(currD.isUserIdCanMingGang(1002));
        Assert.assertFalse(currD.isUserIdCanAnGang(1002));
        Assert.assertFalse(currD.isUserIdCanBuGang(1002));
        Assert.assertFalse(currD.isUserIdCanHu(1002));
    }

    /**
     * 测试一炮多响
     */
    @Test
    public void test_yiPaoDuoXiang() {
        // 构建测试房间
        buildTestRoom();

        ChiPengGangHuSession sessionObj = new ChiPengGangHuSession(MahjongTileDef._1_BING, 1001);
        sessionObj.addUserIdCanHu(1002);
        sessionObj.putUserIdCanChi(1002, new ChiChoiceQuestion(MahjongTileDef._1_BING, true, false, false));
        sessionObj.addUserIdCanHu(1003);
        sessionObj.putUserIdCanPeng(1004);
        sessionObj.putYiPaoDuoXiang(true);

        // 获取当前对话
        ChiPengGangHuSession.Dialog currD = sessionObj.getCurrDialog();

        // 1001 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1001));
        Assert.assertFalse(currD.isUserIdCanPeng(1001));
        Assert.assertFalse(currD.isUserIdCanMingGang(1001));
        Assert.assertFalse(currD.isUserIdCanAnGang(1001));
        Assert.assertFalse(currD.isUserIdCanBuGang(1001));
        Assert.assertFalse(currD.isUserIdCanHu(1001));

        // 1002 只能胡牌, 吃碰杠都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1002));
        Assert.assertFalse(currD.isUserIdCanPeng(1002));
        Assert.assertFalse(currD.isUserIdCanMingGang(1002));
        Assert.assertFalse(currD.isUserIdCanAnGang(1002));
        Assert.assertFalse(currD.isUserIdCanBuGang(1002));
        Assert.assertTrue(currD.isUserIdCanHu(1002));

        // 1003 只能胡牌, 吃碰杠都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1003));
        Assert.assertFalse(currD.isUserIdCanPeng(1003));
        Assert.assertFalse(currD.isUserIdCanMingGang(1003));
        Assert.assertFalse(currD.isUserIdCanAnGang(1003));
        Assert.assertFalse(currD.isUserIdCanBuGang(1003));
        Assert.assertTrue(currD.isUserIdCanHu(1003));

        // 1004 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1004));
        Assert.assertFalse(currD.isUserIdCanPeng(1004));
        Assert.assertFalse(currD.isUserIdCanMingGang(1004));
        Assert.assertFalse(currD.isUserIdCanAnGang(1004));
        Assert.assertFalse(currD.isUserIdCanBuGang(1004));
        Assert.assertFalse(currD.isUserIdCanHu(1004));

        // 擦除 1002
        currD.eraseUserId(1002);
        Assert.assertFalse(currD.isUserIdCanHu(1002));
        Assert.assertTrue(currD.isUserIdCanHu(1003));

        // 移到下一对话
        sessionObj.moveToNextDialog();
        currD = sessionObj.getCurrDialog();

///////////////////////////////////////////////////////////////////////

        // 1001 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1001));
        Assert.assertFalse(currD.isUserIdCanPeng(1001));
        Assert.assertFalse(currD.isUserIdCanMingGang(1001));
        Assert.assertFalse(currD.isUserIdCanAnGang(1001));
        Assert.assertFalse(currD.isUserIdCanBuGang(1001));
        Assert.assertFalse(currD.isUserIdCanHu(1001));

        // 1002 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1002));
        Assert.assertFalse(currD.isUserIdCanPeng(1002));
        Assert.assertFalse(currD.isUserIdCanMingGang(1002));
        Assert.assertFalse(currD.isUserIdCanAnGang(1002));
        Assert.assertFalse(currD.isUserIdCanBuGang(1002));
        Assert.assertFalse(currD.isUserIdCanHu(1002));

        // 1003 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1003));
        Assert.assertFalse(currD.isUserIdCanPeng(1003));
        Assert.assertFalse(currD.isUserIdCanMingGang(1003));
        Assert.assertFalse(currD.isUserIdCanAnGang(1003));
        Assert.assertFalse(currD.isUserIdCanBuGang(1003));
        Assert.assertFalse(currD.isUserIdCanHu(1003));

        // 1004 只能碰牌
        Assert.assertFalse(currD.isUserIdCanChi(1004));
        Assert.assertTrue(currD.isUserIdCanPeng(1004));
        Assert.assertFalse(currD.isUserIdCanMingGang(1004));
        Assert.assertFalse(currD.isUserIdCanAnGang(1004));
        Assert.assertFalse(currD.isUserIdCanBuGang(1004));
        Assert.assertFalse(currD.isUserIdCanHu(1004));

        // 移到下一对话
        sessionObj.moveToNextDialog();
        currD = sessionObj.getCurrDialog();

///////////////////////////////////////////////////////////////////////

        // 1002 只能吃牌
        Assert.assertTrue(currD.isUserIdCanChi(1002));
        Assert.assertFalse(currD.isUserIdCanPeng(1002));
        Assert.assertFalse(currD.isUserIdCanMingGang(1002));
        Assert.assertFalse(currD.isUserIdCanAnGang(1002));
        Assert.assertFalse(currD.isUserIdCanBuGang(1002));
        Assert.assertFalse(currD.isUserIdCanHu(1002));
    }

    /**
     * 测试对话框合并
     */
    @Test
    public void test_dialogMerge() {
        // 构建测试房间
        buildTestRoom();

        ChiPengGangHuSession sessionObj = new ChiPengGangHuSession(MahjongTileDef._1_BING, 1001);
        sessionObj.addUserIdCanHu(1002);
        sessionObj.putUserIdCanMingGang(1002);
        sessionObj.putUserIdCanPeng(1002);
        sessionObj.putUserIdCanChi(1003, new ChiChoiceQuestion(MahjongTileDef._1_BING, true, false, false));
        sessionObj.putYiPaoDuoXiang(false);

        // 获取当前对话
        ChiPengGangHuSession.Dialog currD = sessionObj.getCurrDialog();

        // 1002 所有操作都不可以
        Assert.assertFalse(currD.isUserIdCanChi(1002));
        Assert.assertTrue(currD.isUserIdCanPeng(1002));
        Assert.assertTrue(currD.isUserIdCanMingGang(1002));
        Assert.assertFalse(currD.isUserIdCanAnGang(1002));
        Assert.assertFalse(currD.isUserIdCanBuGang(1002));
        Assert.assertTrue(currD.isUserIdCanHu(1002));
    }
}
