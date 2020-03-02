package red.zhiyuan.mahjongzj.vo;

import lombok.Data;

import java.util.List;

@Data
public class HuVO {
    private List<MahjongVO> mahjongs;
    private String hu;
    private String banker;
}
