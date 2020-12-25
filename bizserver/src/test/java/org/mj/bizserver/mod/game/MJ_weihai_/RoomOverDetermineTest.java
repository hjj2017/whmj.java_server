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
 * 房间结束判定测试
 */
public class RoomOverDetermineTest {
    @Test
    public void test0() {
        Room testRoom = createARoom(4, 0, 1);

// 第 1 局
///////////////////////////////////////////////////////////////////////

        Round newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        // 定庄
        newRound.setBegan(true);
        dingZhuang(testRoom, newRound);

        Player huPlayer = newRound.getPlayerByUserId(3);
        Assert.assertNotNull(huPlayer);
        huPlayer.getCurrState().setHu(true);
        newRound.setEnded(true);

        // 第 1 局, 第 3 个玩家胡牌.
        // 此时还不是所有玩家都坐过庄,
        // 所以牌局不判定为结束...
        Assert.assertFalse(RoomOverDetermine.determine(testRoom));

// 第 2 局
///////////////////////////////////////////////////////////////////////

        newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        // 定庄
        newRound.setBegan(true);
        dingZhuang(testRoom, newRound);

        huPlayer = newRound.getPlayerByUserId(4);
        Assert.assertNotNull(huPlayer);
        huPlayer.getCurrState().setHu(true);
        newRound.setEnded(true);

        // 第 2 局, 第 4 个玩家胡牌.
        // 此时还不是所有玩家都坐过庄,
        // 所以牌局不判定为结束...
        Assert.assertFalse(RoomOverDetermine.determine(testRoom));

// 第 3 局
///////////////////////////////////////////////////////////////////////

        newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        // 定庄
        newRound.setBegan(true);
        dingZhuang(testRoom, newRound);

        huPlayer = newRound.getPlayerByUserId(1);
        Assert.assertNotNull(huPlayer);
        huPlayer.getCurrState().setHu(true);
        newRound.setEnded(true);

        // 第 3 局, 第 1 个玩家胡牌.
        // 此时还不是所有玩家都坐过庄,
        // 所以牌局不判定为结束...
        Assert.assertFalse(RoomOverDetermine.determine(testRoom));

// 第 4 局
///////////////////////////////////////////////////////////////////////

        newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        // 定庄
        newRound.setBegan(true);
        dingZhuang(testRoom, newRound);

        huPlayer = newRound.getPlayerByUserId(4);
        Assert.assertNotNull(huPlayer);
        huPlayer.getCurrState().setHu(true);
        newRound.setEnded(true);

        // 第 4 局, 第 4 个玩家胡牌.
        // 此时虽然所有玩家都坐过庄,
        // 但是第 4 个玩家出现连庄,
        // 此时房间不能判定为结束...
        Assert.assertFalse(RoomOverDetermine.determine(testRoom));

// 第 5 局
///////////////////////////////////////////////////////////////////////

        newRound = testRoom.beginNewRound();
        Assert.assertNotNull(newRound);

        // 定庄
        newRound.setBegan(true);
        dingZhuang(testRoom, newRound);

        huPlayer = newRound.getPlayerByUserId(1);
        Assert.assertNotNull(huPlayer);
        huPlayer.getCurrState().setHu(true);
        newRound.setEnded(true);

        // 第 5 局, 第 1 个玩家胡牌
        Assert.assertTrue(RoomOverDetermine.determine(testRoom));
    }

    @Test
    public void test1() {
        Room testRoom = createARoom(4, 0, 2);

        for (int i = 0; i < 9; i++) {
            Round newRound = testRoom.beginNewRound();
            Assert.assertNotNull(newRound);

            // 定庄
            newRound.setBegan(true);
            dingZhuang(testRoom, newRound);

            Player huPlayer = newRound.getPlayerByUserId(i % 4 + 1);
            Assert.assertNotNull(huPlayer);
            huPlayer.getCurrState().setHu(true);
            newRound.setEnded(true);
        }

        Assert.assertTrue(RoomOverDetermine.determine(testRoom));
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

    /**
     * 定庄
     *
     * @param testRoom  测试房间
     * @param currRound 当前牌局
     */
    static private void dingZhuang(Room testRoom, Round currRound) {
        if (null == testRoom ||
            null == currRound) {
            return;
        }

        int zhuangJiaUserId = DingZhuangStrategy.dingZhuang(testRoom, currRound);

        Player zhuangJiaPlayer = currRound.getPlayerByUserId(zhuangJiaUserId);
        Assert.assertNotNull(zhuangJiaPlayer);
        zhuangJiaPlayer.getCurrState().setZhuangJia(true);
    }
}
