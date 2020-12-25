package org.mj.bizserver.mod.game.MJ_weihai_;

import org.junit.Assert;
import org.junit.Test;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;
import org.mj.bizserver.mod.game.MJ_weihai_.hupattern.HuFormula;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 胡牌公式测试
 */
public class HuFormulaTest {
    @Test
    public void test1() {
        List<MahjongTileDef> mahjongInHand = Arrays.asList(
            MahjongTileDef._1_WAN
        );

        // 测试是否可以胡牌
        boolean canHu = HuFormula.test(mahjongInHand, MahjongTileDef._1_WAN);
        Assert.assertTrue(canHu);

        mahjongInHand = Arrays.asList(
            MahjongTileDef._1_WAN,
            MahjongTileDef._1_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._3_WAN
        );

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._1_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._2_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._3_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._4_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._5_WAN);
        Assert.assertFalse(canHu);

///////////////////////////////////////////////////////////////////////

        mahjongInHand = Arrays.asList(
            MahjongTileDef._1_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._4_WAN
        );

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._1_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._2_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._3_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._4_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._5_WAN);
        Assert.assertFalse(canHu);

///////////////////////////////////////////////////////////////////////

        mahjongInHand = Arrays.asList(
            MahjongTileDef._1_WAN,
            MahjongTileDef._1_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._4_WAN
        );

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._1_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._2_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._3_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._4_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._5_WAN);
        Assert.assertFalse(canHu);

///////////////////////////////////////////////////////////////////////

        mahjongInHand = Arrays.asList(
            MahjongTileDef._1_WAN,
            MahjongTileDef._1_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._3_WAN
        );

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._1_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._2_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._3_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._4_WAN);
        Assert.assertFalse(canHu);

///////////////////////////////////////////////////////////////////////

        mahjongInHand = Arrays.asList(
            MahjongTileDef._2_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef.BAI_BAN,
            MahjongTileDef.BAI_BAN
        );

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._1_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._2_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._3_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._4_WAN);
        Assert.assertFalse(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef.BAI_BAN);
        Assert.assertTrue(canHu);

///////////////////////////////////////////////////////////////////////

        mahjongInHand = Arrays.asList(
            MahjongTileDef._1_WAN,
            MahjongTileDef._1_WAN,

            MahjongTileDef._1_WAN,
            MahjongTileDef._2_WAN,

            MahjongTileDef._3_WAN,
            MahjongTileDef._4_WAN,
            MahjongTileDef._5_WAN,

            MahjongTileDef._6_WAN,
            MahjongTileDef._7_WAN,
            MahjongTileDef._8_WAN,

            MahjongTileDef._9_WAN,
            MahjongTileDef._9_WAN,
            MahjongTileDef._9_WAN
        );

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._1_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._2_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._3_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._4_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._5_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._6_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._7_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._8_WAN);
        Assert.assertTrue(canHu);

        // 测试是否可以胡牌
        canHu = HuFormula.test(mahjongInHand, MahjongTileDef._9_WAN);
        Assert.assertTrue(canHu);

        Set<MahjongTileDef> canHuMahjongSet = HuFormula.getCanHuMahjongSet(mahjongInHand);
        Assert.assertTrue(canHuMahjongSet.containsAll(Arrays.asList(
            MahjongTileDef._1_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._4_WAN,
            MahjongTileDef._6_WAN,
            MahjongTileDef._7_WAN,
            MahjongTileDef._8_WAN,
            MahjongTileDef._9_WAN
        )));
    }

    @Test
    public void test2() {
        List<MahjongTileDef> mahjongInHand = List.of(
            MahjongTileDef._1_WAN,
            MahjongTileDef._1_WAN,
            MahjongTileDef._1_WAN,
            MahjongTileDef._4_TIAO,
            MahjongTileDef._6_TIAO,
            MahjongTileDef._3_BING,
            MahjongTileDef._3_BING,
            MahjongTileDef._3_BING,
            MahjongTileDef._4_BING,
            MahjongTileDef._4_BING,
            MahjongTileDef._4_BING,
            MahjongTileDef._5_BING,
            MahjongTileDef._5_BING
        );

        boolean canHu = HuFormula.test(
            mahjongInHand,
            MahjongTileDef._5_TIAO
        );

        Assert.assertTrue(canHu);
    }

    @Test
    public void test3() {
        List<MahjongTileDef> mahjongInHand = List.of(
            MahjongTileDef._3_WAN,
            MahjongTileDef._4_WAN,
            MahjongTileDef._5_WAN,
            MahjongTileDef._1_TIAO,
            MahjongTileDef._2_TIAO,
            MahjongTileDef._3_TIAO,
            MahjongTileDef._4_TIAO,
            MahjongTileDef._5_TIAO,
            MahjongTileDef._6_TIAO,
            MahjongTileDef._7_TIAO
        );

        boolean canHu = HuFormula.test(
            mahjongInHand,
            MahjongTileDef._1_TIAO
        );

        Assert.assertTrue(canHu);
    }

    @Test
    public void test4() {
        List<MahjongTileDef> mahjongInHand = Arrays.asList(
            MahjongTileDef._1_WAN,
            MahjongTileDef._1_WAN,
            MahjongTileDef._1_WAN,
            MahjongTileDef._2_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._4_WAN,
            MahjongTileDef._5_WAN,
            MahjongTileDef._6_WAN,
            MahjongTileDef._7_WAN,
            MahjongTileDef._8_WAN,
            MahjongTileDef._9_WAN,
            MahjongTileDef._9_WAN,
            MahjongTileDef._9_WAN
        );

        // 测试是否可以胡牌
        boolean canHu = HuFormula.test(mahjongInHand, MahjongTileDef._5_WAN);
        Assert.assertTrue(canHu);
    }

    @Test
    public void test5() {
        List<MahjongTileDef> mahjongInHand = Arrays.asList(
            MahjongTileDef._2_WAN,
            MahjongTileDef._3_WAN,
            MahjongTileDef._4_WAN,
            MahjongTileDef._6_TIAO,
            MahjongTileDef._7_TIAO,
            MahjongTileDef._1_BING,
            MahjongTileDef._2_BING,
            MahjongTileDef._3_BING,
            MahjongTileDef._3_BING,
            MahjongTileDef._3_BING,
            MahjongTileDef._4_BING,
            MahjongTileDef._5_BING,
            MahjongTileDef._6_BING
        );

        // 测试是否可以胡牌
        boolean canHu = HuFormula.test(mahjongInHand, MahjongTileDef._5_TIAO);
        Assert.assertTrue(canHu);
    }
}
