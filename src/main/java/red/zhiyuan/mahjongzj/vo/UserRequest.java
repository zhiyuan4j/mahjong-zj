package red.zhiyuan.mahjongzj.vo;

import lombok.Data;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
@Data
public class UserRequest {
    private Integer roomId;
    private String type;
    private String mahjong;
}
