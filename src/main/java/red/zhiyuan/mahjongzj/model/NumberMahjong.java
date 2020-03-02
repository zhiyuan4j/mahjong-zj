package red.zhiyuan.mahjongzj.model;

import java.util.Objects;

/**
 * @author zhiyuan.wang
 * @date 2020/1/26
 */
public class NumberMahjong extends BaseMahjong {
    private Integer number;

    public NumberMahjong(Integer type) {
        super(type);
    }

    public NumberMahjong() {
    }

    public NumberMahjong(Integer type, Integer number) {
        super(type);
        this.number = number;
    }


    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    private String getNumberStringDesc() {
        if (number == null) {
            return "";
        }
        switch (number) {
            case 1: return "一";
            case 2: return "二";
            case 3: return "三";
            case 4: return "四";
            case 5: return "五";
            case 6: return "六";
            case 7: return "七";
            case 8: return "八";
            case 9: return "九";
            default: return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NumberMahjong mahjong = (NumberMahjong) o;
        return Objects.equals(number, mahjong.number);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), number);
    }

    @Override
    public String toString() {
        return getNumberStringDesc() + super.toString();
    }
}
