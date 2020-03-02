package red.zhiyuan.mahjongzj.factory;

import red.zhiyuan.mahjongzj.enums.MahjongTypeEnum;
import red.zhiyuan.mahjongzj.exception.InvalidMahjongException;
import red.zhiyuan.mahjongzj.model.BaseMahjong;
import red.zhiyuan.mahjongzj.model.NumberMahjong;

/**
 * @author zhiyuan.wang
 * @date 2020/2/1
 */
public class MahjongFactory {

    public static BaseMahjong createMahjong(Integer type, Integer number) {
        MahjongTypeEnum typeEnum = MahjongTypeEnum.valueOf(type);
        if (typeEnum == null) {
            throw new InvalidMahjongException();
        }
        switch (typeEnum) {
            case TIAO:
            case BING:
            case WAN:
                if (number == null) {
                    throw new InvalidMahjongException();
                }
                return new NumberMahjong(type, number);
            default:
                return new BaseMahjong(type);
        }

    }
}
