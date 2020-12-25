package org.mj.bizserver.mod.game.MJ_weihai_.hupattern;

import org.mj.bizserver.mod.game.MJ_weihai_.bizdata.MahjongTileDef;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 胡牌公式
 */
final public class HuFormula {
    /**
     * 私有化类默认构造器
     */
    private HuFormula() {
    }

    /**
     * 获取可以胡牌的麻将集合
     *
     * @param mahjongInHand 手牌列表
     * @return 可以胡牌的麻将集合
     */
    static public Set<MahjongTileDef> getCanHuMahjongSet(final List<MahjongTileDef> mahjongInHand) {
        if (null == mahjongInHand ||
            mahjongInHand.size() < 1) {
            return null;
        }

        if (1 != mahjongInHand.size() % 3) {
            // 手里的麻将牌的数量必须是 13、10、7、4、1,
            // 否则无法执行接下来的逻辑...
            return null;
        }

        if (1 == mahjongInHand.size()) {
            // 如果手里就剩下最后一张牌了,
            // 那么最后一张牌就是可以胡的牌 ( 因为只差凑出最后的对子了 )
            return Set.of(mahjongInHand.get(0));
        }

        // 可以胡牌的麻将牌集合
        final Set<MahjongTileDef> canHuMahjongSet = new HashSet<>();

        for (MahjongTileDef currT : mahjongInHand) {
            if (null == currT) {
                continue;
            }

            // 添加到集合避免重复判断
            canHuMahjongSet.add(currT);

            if (MahjongTileDef.isWanTiaoBing(currT)) {
                // 如果当前麻将牌是万、条、饼,
                // 那么就看看当前麻将牌两侧的牌是否可以胡牌,
                // 比方说当前麻将牌是二万,
                // 就看看一万和三万是不是可以胡牌...
                MahjongTileDef t0 = MahjongTileDef.valueOf(currT.getIntVal() - 1);
                MahjongTileDef t1 = MahjongTileDef.valueOf(currT.getIntVal() + 1);

                if (null != t0) {
                    canHuMahjongSet.add(t0);
                }

                if (null != t1) {
                    canHuMahjongSet.add(t1);
                }
            }
        }

        // 移除所有不能胡的牌
        canHuMahjongSet.removeIf((currT) -> !test(mahjongInHand, currT));

        return canHuMahjongSet;
    }

    /**
     * 测试是否可以胡牌
     *
     * @param mahjongInHand 手中的麻将牌列表
     * @param mahjongAtLast 最后一张麻将牌
     * @return true = 可以胡牌, false = 不能胡牌
     */
    static public boolean test(final List<MahjongTileDef> mahjongInHand, final MahjongTileDef mahjongAtLast) {
        if (null == mahjongInHand ||
            mahjongInHand.size() <= 0 ||
            null == mahjongAtLast) {
            return false;
        }

        if (1 != mahjongInHand.size() % 3) {
            // 手里的麻将牌的数量必须是 13、10、7、4、1,
            // 否则无法执行接下来的逻辑...
            return false;
        }

        if (1 == mahjongInHand.size()) {
            // 如果手里就剩下最后一张牌了,
            // 那就看看能不能凑成对子
            return mahjongInHand.get(0) == mahjongAtLast;
        }

        // 不可能的数量
        int impossibleCount = 0;

        for (MahjongTileDef currT : mahjongInHand) {
            if (null != currT &&
                currT == mahjongAtLast) {
                // 统计一下手里的麻将牌有多少张和最后的麻将牌是一样的
                ++impossibleCount;
            }
        }

        if (impossibleCount >= 4) {
            // 如果手里已有 4 张牌和最后的麻将牌一样,
            // 那么最后的这张牌不可能胡牌!
            // 例如手里有 4 张一饼,
            // 那么就不可能摸到第 5 张一饼胡牌...
            return false;
        }

        // 测试列表
        final List<MahjongTileDef> tTestList = new ArrayList<>(14);
        tTestList.addAll(mahjongInHand);
        tTestList.add(mahjongAtLast);
        tTestList.sort(Comparator.comparingInt(MahjongTileDef::getIntVal));

        // 看看是不是七小对...
        if (isQiXiaoDui(tTestList)) {
            return true;
        }

        // 最大测试次数
        final int maxTestTimez = tTestList.size();
        // 创建临时列表
        final List<MahjongTileDef> tTempList = new ArrayList<>(maxTestTimez);
        // 已经测试的对子
        final Set<MahjongTileDef> testedDuiZi = new HashSet<>();

        for (int i = 0; i < maxTestTimez - 1; i++) {
            // 首先找到对子出现的位置,
            if (tTestList.get(i) != tTestList.get(i + 1)) {
                continue;
            }

            // 要移除的对子
            MahjongTileDef removingDuiZi = tTestList.get(i);

            if (testedDuiZi.contains(removingDuiZi)) {
                // 如果已经测试过这个对子,
                continue;
            }

            testedDuiZi.add(removingDuiZi);

            // 检查剩下的牌是否可以凑成顺子或者刻子
            //
            for (int j = 0; j < maxTestTimez - 4; j++) {
                // 重新初始化临时列表
                tTempList.clear();
                tTempList.addAll(tTestList);

                // 移除指定的对子
                tTempList.remove(removingDuiZi);
                tTempList.remove(removingDuiZi);

                // 剪切掉从指定位置开始所有的顺子
                cutAllShunZi(tTempList, j);
                // 剪切掉所有的刻子
                cutAllKeZi(tTempList);

                if (tTempList.size() > 2) {
                    // 如果出现这种情况,
                    // 则说明在当前位置 i 之前还存在顺子,
                    // 尝试剪切掉剩下的顺子...
                    cutAllShunZi(tTempList, 0);
                    // 为什么还要再次剪掉顺子,
                    // 可以思考这样的情况:
                    // 81, 82, 83, 83, 83, 83, 84, 85, 86,
                }

                if (tTempList.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 是否是七小对
     *
     * @param tSortedList 已排序的麻将牌列表
     * @return true = 是七小对, false = 不是七小对
     */
    static private boolean isQiXiaoDui(List<MahjongTileDef> tSortedList) {
        if (null == tSortedList ||
            14 != tSortedList.size()) {
            return false;
        }

        for (int i = 0; i < tSortedList.size(); i += 2) {
            final MahjongTileDef t0 = tSortedList.get(i);
            final MahjongTileDef t1 = tSortedList.get(i + 1);

            if (null == t0 ||
                t0 != t1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 从麻将牌列表中剪掉顺子牌,
     * 例如: tSortedList = [ 21, 22, 23, 41, 43, 44, ], startIndex = 0, 处理之后 => [ 41, 43, 44, ];
     *
     * XXX 注意下面这个例子: 从 1 开始向后找, 凑不出顺子
     * 例如: tSortedList = [ 21, 22, 23, 41, 43, 44, ], startIndex = 1, 处理之后 => [ 21, 22, 23, 41, 43, 44, ];
     *
     * 即使数值上不连续, 顺子也会被剪掉,
     * 例如: tSortedList = [ 21, 21, 22, 22, 23, 25, ], startIndex = 0, 处理之后 => [ 21, 22, 25, ];
     * 例如: tSortedList = [ 21, 21, 22, 22, 23, 25, ], startIndex = 1, 处理之后 => [ 21, 22, 25, ];
     *
     * XXX 注意下面这个例子: 从 2 开始向后找, 凑不出顺子
     * 例如: tSortedList = [ 21, 21, 22, 22, 23, 25, ], startIndex = 2, 处理之后 => [ 21, 21, 22, 22, 23, 25, ];
     *
     * @param tSortedList 已排序的麻将牌列表
     * @param startIndex  起始索引
     */
    static private void cutAllShunZi(List<MahjongTileDef> tSortedList, int startIndex) {
        if (null == tSortedList ||
            startIndex > tSortedList.size() - 3) {
            return;
        }

        for (int i = startIndex; i <= tSortedList.size() - 3; ) {
            // 获取第一张牌
            MahjongTileDef t0 = tSortedList.get(i);

            if (null == t0) {
                ++i;
                continue;
            }

            if (!MahjongTileDef.isWanTiaoBing(t0)) {
                // 非万、条、饼花色的麻将牌不会有顺子
                ++i;
                continue;
            }

            MahjongTileDef t1 = MahjongTileDef.valueOf(t0.getIntVal() + 1);
            MahjongTileDef t2 = MahjongTileDef.valueOf(t0.getIntVal() + 2);

            if (null == t1 ||
                null == t2) {
                ++i;
                continue;
            }

            if (!tSortedList.contains(t1) ||
                !tSortedList.contains(t2)) {
                ++i;
                continue;
            }

            // 存在三张连续的麻将牌
            tSortedList.remove(t0);
            tSortedList.remove(t1);
            tSortedList.remove(t2);
        }
    }

    /**
     * 剪切所有的刻子
     *
     * @param tSortedList 已排序的麻将牌列表
     */
    static private void cutAllKeZi(List<MahjongTileDef> tSortedList) {
        if (null == tSortedList) {
            return;
        }

        for (int i = 0; i <= tSortedList.size() - 3; ) {
            // 获取连续的三张牌
            final MahjongTileDef t0 = tSortedList.get(i);
            final MahjongTileDef t1 = tSortedList.get(i + 1);
            final MahjongTileDef t2 = tSortedList.get(i + 2);

            if (null == t0 ||
                t0 != t1 ||
                t0 != t2) {
                ++i;
                continue;
            }

            // 如果三张牌相同 ( 刻子 ),
            tSortedList.remove(t0);
            tSortedList.remove(t1);
            tSortedList.remove(t2);
        }
    }
}
