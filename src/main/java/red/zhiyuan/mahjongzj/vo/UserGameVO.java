package red.zhiyuan.mahjongzj.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhiyuan.wang
 * @date 2020/2/15
 */
@Data
public class UserGameVO implements Serializable {
    private List<UserVO> userPublics;
    private List<MahjongVO> privateMahjongs;

    private Integer leftMahjongCount;
    private MahjongVO baida;

    private List<MahjongVO> firedMahjongs;

    private MahjongVO dispatch;
    private Boolean canDispatch;
    private Boolean peng;
    private Boolean gang;
    private Boolean hu;
    private Boolean myTurn;
    private Boolean hasOperation;
    private Boolean myOperation;
    private String userId;
    private HuVO userHu;
}
