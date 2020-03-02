package red.zhiyuan.mahjongzj.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
@Data
public class GameVO {

    private Integer roomId;

    private String roomPublic;

    private List<String> players;

    private String roomer;

    private String myId;

}
