package red.zhiyuan.mahjongzj.model;

import red.zhiyuan.mahjongzj.enums.MahjongTypeEnum;
import red.zhiyuan.mahjongzj.exception.InvalidMahjongException;

import java.io.Serializable;
import java.util.Objects;

/**
 * type:
 * 1    条
 * 2    饼
 * 3    万
 * 4    东
 * 5    南
 * 6    西
 * 7    北
 * 8    中
 * 9    发
 * 10   白
 * @author zhiyuan.wang
 * @date 2020/1/26
 */
public class BaseMahjong implements Serializable {

    /**
     * @see MahjongTypeEnum
     */
    private Integer type;

    public BaseMahjong() {
    }

    public BaseMahjong(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseMahjong that = (BaseMahjong) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type);
    }

    @Override
    public String toString() {

        MahjongTypeEnum type = MahjongTypeEnum.valueOf(this.type);

        if (type == null) {
            throw new InvalidMahjongException();
        }

        return type.getName();

    }
}
