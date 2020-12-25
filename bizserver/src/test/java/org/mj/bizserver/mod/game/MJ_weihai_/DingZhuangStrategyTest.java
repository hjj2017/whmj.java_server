package org.mj.bizserver.mod.game.MJ_weihai_;

import org.junit.Assert;
import org.junit.Test;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Player;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Room;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.Round;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.RuleSetting;

import java.util.HashMap;
import java.util.Map;

/**
 * 定庄策略测试
 */
public class DingZhuangStrategyTest {
    @Test
    public void test0() {
        //
        // 测试开局定庄
        //
        Room testRoom = createARoom(4, 0, 1);
        Round newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        newRound.setBegan(true);
        int zhuangJiaUserId = DingZhuangStrategy.dingZhuang(testRoom, newRound);
        Assert.assertEquals(zhuangJiaUserId, 1);
    }

    @Test
    public void test1() {
        //
        // 第一局庄家胡牌, 那么第二局庄家继续坐庄
        //
        Room testRoom = createARoom(4, 0, 1);
        Round newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        newRound.setBegan(true);
        int zhuangJiaUserId = DingZhuangStrategy.dingZhuang(testRoom, newRound);
        Player zhuangJiaPlayer = newRound.getPlayerByUserId(zhuangJiaUserId);
        Assert.assertNotNull(zhuangJiaPlayer);

        // 庄家胡牌
        zhuangJiaPlayer.getCurrState().setZhuangJia(true);
        zhuangJiaPlayer.getCurrState().setZiMo(true);
        newRound.setEnded(true);

        newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        newRound.setBegan(true);
        zhuangJiaUserId = DingZhuangStrategy.dingZhuang(testRoom, newRound);
        Assert.assertEquals(zhuangJiaUserId, 1);
    }

    @Test
    public void test2() {
        //
        // 第一局庄家未胡牌, 那么第二局下家坐庄
        //
        Room testRoom = createARoom(4, 0, 1);
        Round newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        newRound.setBegan(true);
        int zhuangJiaUserId = DingZhuangStrategy.dingZhuang(testRoom, newRound);
        Player zhuangJiaPlayer = newRound.getPlayerByUserId(zhuangJiaUserId);
        Assert.assertNotNull(zhuangJiaPlayer);

        zhuangJiaPlayer.getCurrState().setZhuangJia(true);
        Player huPlayer = newRound.getPlayerByUserId(3);
        Assert.assertNotNull(huPlayer);
        huPlayer.getCurrState().setHu(true);
        newRound.setEnded(true);

        newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        newRound.setBegan(true);
        zhuangJiaUserId = DingZhuangStrategy.dingZhuang(testRoom, newRound);
        Assert.assertEquals(zhuangJiaUserId, 2);
    }

    @Test
    public void test3() {
        //
        // 如果按照局来算
        // 第一局庄家未胡牌, 那么第二局胡牌玩家坐庄
        //
        Room testRoom = createARoom(4, 4, 0);
        Round newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        newRound.setBegan(true);
        int zhuangJiaUserId = DingZhuangStrategy.dingZhuang(testRoom, newRound);
        Player zhuangJiaPlayer = newRound.getPlayerByUserId(zhuangJiaUserId);
        Assert.assertNotNull(zhuangJiaPlayer);

        zhuangJiaPlayer.getCurrState().setZhuangJia(true);
        Player huPlayer = newRound.getPlayerByUserId(3);
        Assert.assertNotNull(huPlayer);
        huPlayer.getCurrState().setHu(true);
        newRound.setEnded(true);

        newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        newRound.setBegan(true);
        zhuangJiaUserId = DingZhuangStrategy.dingZhuang(testRoom, newRound);
        Assert.assertEquals(zhuangJiaUserId, 3);
    }

    /**
     * 创建一个房间
     *
     * @param maxPlayerz 最大玩家数量
     * @param maxRound   最大局数
     * @param maxCircle  最大圈数
     * @return 房间
     */
    static private Room createARoom(
        final int maxPlayerz, final int maxRound, final int maxCircle) {

        Map<Integer, Integer> ruleMap = new HashMap<>();
        ruleMap.put(1002, 1);          // 支付方式
        ruleMap.put(1003, maxPlayerz); // 最大人数
        ruleMap.put(1004, maxRound);   // 最大局数
        ruleMap.put(1005, maxCircle);  // 最大圈数
        RuleSetting ruleSetting = new RuleSetting(ruleMap);

        Room testRoom = new Room(123456, ruleSetting, 1);

        for (int i = 0; i < maxPlayerz; i++) {
            Player newPlayer = new Player(i + 1);
            newPlayer.setUserName("test_" + i);
            testRoom.playerSitDown(newPlayer);
        }

        return testRoom;
    }
}
