package red.zhiyuan.mahjongzj.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
@Data
public class RoomVO implements Serializable {
    private Integer roomId;
    private int playerCount;
    private String roomer;
}
