package red.zhiyuan.mahjongzj.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhiyuan.wang
 * @date 2020/2/15
 */
@Data
public class UserVO implements Serializable {
    private String userId;

    private List<MahjongVO> firedMahjongs;

    private List<List<MahjongVO>> publicMahjongs;

}
