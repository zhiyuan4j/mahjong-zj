package red.zhiyuan.mahjongzj.enums;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author zhiyuan.wang
 * @date 2020/2/1
 */
public enum MahjongTypeEnum {
    TIAO(1, "条"),
    BING(2, "饼"),
    WAN(3, "万"),
    DONG(4, "东风"),
    NAN(5, "南风"),
    XI(6, "西风"),
    BEI(7, "北风"),
    ZHONG(8, "红中"),
    FA(9, "发财"),
    BAI(10, "白板"),
    ;
    private int type;
    private String name;

    public static final Map<Integer, MahjongTypeEnum> TYPE_MAP = Maps.newHashMap();

    static {
        for (MahjongTypeEnum item : MahjongTypeEnum.values()) {
            TYPE_MAP.put(item.getType(), item);
        }
    }

    MahjongTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public static MahjongTypeEnum valueOf(Integer type) {
        if (type == null) {
            return null;
        }
        return TYPE_MAP.get(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
