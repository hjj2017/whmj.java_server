package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.junit.Assert;
import org.junit.Test;
import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.List;

/**
 * 夹胡测试
 */
public class Pattern_JiaHuTest {
    @Test
    public void test1() {
        boolean yes = new Pattern_JiaHu().test(null, List.of(
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

        ), MahjongTileDef._5_TIAO);

        Assert.assertTrue(yes);
    }
}
