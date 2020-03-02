package red.zhiyuan.mahjongzj.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import red.zhiyuan.mahjongzj.enums.MahjongTypeEnum;
import red.zhiyuan.mahjongzj.factory.MahjongFactory;
import red.zhiyuan.mahjongzj.model.BaseMahjong;
import red.zhiyuan.mahjongzj.model.NumberMahjong;
import red.zhiyuan.mahjongzj.struct.CycleLink;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhiyuan.wang
 * @date 2020/2/1
 */
public class MahjongUtil {

    public static CycleLink<BaseMahjong> numberMahjongLink = new CycleLink<>();
    public static CycleLink<BaseMahjong> fengMahjongLink = new CycleLink<>();
    public static CycleLink<BaseMahjong> specialMahjongLink = new CycleLink<>();
    public static final Map<String, BaseMahjong> MAHJONG_MAP = Maps.newHashMap();

    static {
        Stream.of(MahjongTypeEnum.values()).forEach(typeEnum -> {
            switch (typeEnum) {
                case TIAO:
                case BING:
                case WAN:
                    for (int i=1; i<=9; i++) {
                        BaseMahjong mahjong = MahjongFactory.createMahjong(typeEnum.getType(), i);
                        MAHJONG_MAP.put(mahjong.toString(), mahjong);
                    }
                    break;
                default:
                    BaseMahjong mahjong = MahjongFactory.createMahjong(typeEnum.getType(), null);
                    MAHJONG_MAP.put(mahjong.toString(), mahjong);
            }
        });

    }

    public static List<BaseMahjong> createZhenJiangMahjongs() {
        List<BaseMahjong> all = Lists.newArrayList();
        all.addAll(createNumberMahjong(MahjongTypeEnum.TIAO.getType()));
        all.addAll(createNumberMahjong(MahjongTypeEnum.BING.getType()));
        all.addAll(createNumberMahjong(MahjongTypeEnum.WAN.getType()));
        all.addAll(createSpecialMahjong(MahjongTypeEnum.DONG.getType()));
        all.addAll(createSpecialMahjong(MahjongTypeEnum.NAN.getType()));
        all.addAll(createSpecialMahjong(MahjongTypeEnum.XI.getType()));
        all.addAll(createSpecialMahjong(MahjongTypeEnum.BEI.getType()));
        all.addAll(createSpecialMahjong(MahjongTypeEnum.ZHONG.getType()));
        all.addAll(createSpecialMahjong(MahjongTypeEnum.FA.getType()));
        all.addAll(createSpecialMahjong(MahjongTypeEnum.BAI.getType()));
        return all;
    }

    public static void sortMohjongs(List<BaseMahjong> mahjongs) {
        mahjongs.sort(mahjongComparator());
    }


    public static boolean hasPeng(String mahjong, List<BaseMahjong> privateMahjongs) {

        if (StringUtils.isBlank(mahjong)) {
            return false;
        }

        int count = 0;

        for (BaseMahjong current : privateMahjongs) {
            if (current.toString().equals(mahjong)) {
                count++;
            }
            if (count == 2) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMingGang(String mahjong, List<BaseMahjong> privateMahjongs) {

        int count = 0;

        if (StringUtils.isBlank(mahjong)) {
            return false;
        } else {
            for (BaseMahjong current : privateMahjongs) {
                if (current.toString().equals(mahjong)) {
                    count++;
                }
                if (count == 3) {
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean hasAnGang(List<BaseMahjong> privateMahjongs) {

        Map<String, Long> grouped = privateMahjongs.stream()
                .collect(Collectors.groupingBy(BaseMahjong::toString, Collectors.counting()));

        for (Map.Entry<String, Long> group : grouped.entrySet()) {
            if (group.getValue() == 4) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasHu(List<BaseMahjong> privateMahjongs, String baida) {
        return !CollectionUtils.isEmpty(hu(privateMahjongs, baida));
    }

    public static List<List<BaseMahjong>> hu(List<BaseMahjong> privateMahjongs, String baida) {
        List<List<BaseMahjong>> pinghu = hu(privateMahjongs, 1, (privateMahjongs.size() - 1) / 3, Lists.newArrayList(), baida);
        if (CollectionUtils.isEmpty(pinghu)) {
            return hu(privateMahjongs, 7, 0, Lists.newArrayList(), baida);
        } else {
            return pinghu;
        }
    }

    public static List<List<BaseMahjong>> hu(List<BaseMahjong> mahjongs,  int twoNum, int threeNum, List<List<BaseMahjong>> saveMahjongTileses, String baida) {
        if (CollectionUtils.isEmpty(mahjongs)) {
            if (twoNum == 0 && threeNum == 0) {
                return saveMahjongTileses;
            } else {
                return Lists.newArrayList();
            }
        }

        if (mahjongs.size() == (twoNum * 2) + (threeNum * 3)) {
            if (threeNum > 0) {
                int[][] indexs = siphonThreeIndexs(mahjongs.size());
                if (indexs == null) {
                    return Lists.newArrayList();
                }

                for (int[] index : indexs) {
                    BaseMahjong a = mahjongs.get(index[0]);
                    BaseMahjong b = mahjongs.get(index[1]);
                    BaseMahjong c = mahjongs.get(index[2]);
                    if (isCanThree(a, b, c, baida)) {
                        List<List<BaseMahjong>> newSaved = add(saveMahjongTileses, Lists.newArrayList(a, b, c));
                        List<BaseMahjong> newShortList = remove(mahjongs, Lists.newArrayList(index[0], index[1], index[2]));
                        List<List<BaseMahjong>> res = hu(newShortList, twoNum, threeNum - 1, newSaved, baida);
                        if (!CollectionUtils.isEmpty(res)) {
                            return res;
                        }
                    }
                }
            } else if (twoNum > 0) {
                int[][] indexs = siphonTwoIndexs(mahjongs.size());
                if (indexs == null) {
                    return Lists.newArrayList();
                }

                for (int[] index : indexs) {
                    BaseMahjong a = mahjongs.get(index[0]);
                    BaseMahjong b = mahjongs.get(index[1]);
                    if (isCanTwo(a, b, baida)) {
                        List<List<BaseMahjong>> newSaved = add(saveMahjongTileses, Lists.newArrayList(a, b));
                        List<BaseMahjong> left = remove(mahjongs, Lists.newArrayList(index[0], index[1]));
                        List<List<BaseMahjong>> res = hu(left, twoNum - 1, threeNum, newSaved, baida);
                        if (!CollectionUtils.isEmpty(res)) {
                            return res;
                        }
                    }
                }
            } else {
                return saveMahjongTileses;
            }

        }

        return Lists.newArrayList();
    }

    private static List<List<BaseMahjong>> add(List<List<BaseMahjong>> list, List<BaseMahjong> all) {
        List<List<BaseMahjong>> b = Lists.newArrayList();
        for (int i=0; i<list.size(); i++) {
            List<BaseMahjong> item = list.get(i);
            b.add(item);
        }
        b.add(all);
        return b;
    }

    private static List<BaseMahjong> remove(List<BaseMahjong> list, ArrayList<Integer> integers) {

        List<BaseMahjong> b = Lists.newArrayList();
        for (int i=0; i<list.size(); i++) {
            if (integers.contains(i)) {
                continue;
            }
            BaseMahjong item = list.get(i);
            if (item.getType()<=3) {
                NumberMahjong numberMahjong = new NumberMahjong();
                numberMahjong.setNumber(((NumberMahjong)item).getNumber());
                numberMahjong.setType(((NumberMahjong)item).getType());
                b.add(numberMahjong);
            } else {
                BaseMahjong mahjong = new BaseMahjong();
                mahjong.setType(item.getType());
                b.add(mahjong);
            }
        }
        return b;

    }

    private static boolean isCanTwo(BaseMahjong a, BaseMahjong b, String baida) {
        if (a.toString().equals(b.toString())) {
            return true;
        }

        if (baida == null) {
            return false;
        }

        if (a.toString().equals(baida) || b.toString().equals(baida)) {
            return true;
        }
        return false;
    }

    private static boolean isCanThree(BaseMahjong a, BaseMahjong b, BaseMahjong c, String da) {
        if (a.getType().equals(b.getType()) && b.getType().equals(c.getType())) {
            if (a.getType() > 3) {
                return true;
            }
            if (numberLink(a,b,c) ||
                    (a.toString().equals(b.toString()) && b.toString().equals(c.toString()))) {
                return true;
            }
        }

        if (da == null) {
            return false;
        }

        List<BaseMahjong> list = Lists.newArrayList(a,b,c);
        List<BaseMahjong> daList = list.stream().filter(mh -> mh.toString().equals(da)).collect(Collectors.toList());
        List<BaseMahjong> noList = list.stream().filter(mh -> !mh.toString().equals(da)).collect(Collectors.toList());

        if (daList.size() == 0) {
            return false;
        }

        // 不可能为3 ，为3的话，在上面就return true了
        if (daList.size() == 2) {
            return true;
        }

        // noList.size = 2
        if (noList.get(0).getType().equals(noList.get(1).getType())) {
            if (noList.get(0).getType() > 3) {
                return true;
            }
            if (Math.abs(((NumberMahjong)noList.get(1)).getNumber() - ((NumberMahjong)noList.get(0)).getNumber()) <= 2) {
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    private static boolean numberLink(BaseMahjong a, BaseMahjong b, BaseMahjong c) {

        ArrayList<BaseMahjong> mah = Lists.newArrayList(a, b, c);
        sortMohjongs(mah);

        if (((NumberMahjong)(mah.get(1))).getNumber() - ((NumberMahjong)(mah.get(0))).getNumber() != 1) {
            return false;
        }

        if (((NumberMahjong)(mah.get(2))).getNumber() - ((NumberMahjong)(mah.get(1))).getNumber() != 1) {
            return false;
        }

        return true;
    }

    //从数组长度为arrayLen的整形数组中任意抽取两个元素，把所有可能的组合的索引列成一个二位数组返回出来
    private static int[][] siphonThreeIndexs(int arrayLen) {
        int len = (arrayLen * (arrayLen - 1) * (arrayLen - 2)) / 6;
        if (len > 0) {
            int[][] indexs = new int[len][3];
            int index = 0;
            for (int i = 0; i < arrayLen; i++) {
                for (int j = (i + 1); j < arrayLen; j++) {
                    for (int k = (j + 1); k < arrayLen; k++) {
                        indexs[index][0] = i;
                        indexs[index][1] = j;
                        indexs[index][2] = k;
                        index++;
                    }
                }
            }
            return indexs;
        } else {
            return null;
        }
    }

    //从数组长度为arrayLen的整形数组中任意抽取两个元素，把所有可能的组合的索引列成一个二位数组返回出来
    private static int[][] siphonTwoIndexs(int arrayLen) {
        int len = (arrayLen * (arrayLen - 1)) / 2;
        if (len > 0) {
            int[][] indexs = new int[len][2];
            int index = 0;
            for (int i = 0; i < arrayLen; i++) {
                for (int j = (i + 1); j < arrayLen; j++) {
                    indexs[index][0] = i;
                    indexs[index][1] = j;
                    index++;
                }
            }

            return indexs;
        } else {
            return null;
        }
    }


    private static Comparator<BaseMahjong> mahjongComparator() {

        return (a, b) -> {
            int deltType = a.getType() - b.getType();

            if (deltType != 0) {
                return deltType;
            }

            if (a.getType() <= 3) {
                return ((NumberMahjong)a).getNumber() - ((NumberMahjong)b).getNumber();
            } else {
                return 0;
            }
        };

    }

    private static List<BaseMahjong> createNumberMahjong(Integer type) {
        List<BaseMahjong> mahjongs = Lists.newArrayList();
        for (int i=1; i<=9; i++) {
            mahjongs.add(MahjongFactory.createMahjong(type, i));
            mahjongs.add(MahjongFactory.createMahjong(type, i));
            mahjongs.add(MahjongFactory.createMahjong(type, i));
            mahjongs.add(MahjongFactory.createMahjong(type, i));
            numberMahjongLink.add(MahjongFactory.createMahjong(type, i));
        }
        return mahjongs;
    }

    private static List<BaseMahjong> createSpecialMahjong(Integer type) {
        List<BaseMahjong> mahjongs = Lists.newArrayList();
        mahjongs.add(MahjongFactory.createMahjong(type, null));
        mahjongs.add(MahjongFactory.createMahjong(type, null));
        mahjongs.add(MahjongFactory.createMahjong(type, null));
        mahjongs.add(MahjongFactory.createMahjong(type, null));
        BaseMahjong mahjong = MahjongFactory.createMahjong(type, null);
        if (type >= 4 && type <= 7) {
            fengMahjongLink.add(mahjong);
        } else {
            specialMahjongLink.add(mahjong);
        }
        return mahjongs;
    }

}
