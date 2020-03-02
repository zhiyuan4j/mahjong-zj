package red.zhiyuan.mahjongzj.bean;

import com.google.common.collect.Lists;
import lombok.Data;
import red.zhiyuan.mahjongzj.model.User;

import java.util.List;

/**
 * @author zhiyuan.wang
 * @date 2020/2/1
 */
@Data
public class Room {

    private Integer roomId;

    private Boolean roomPublic;

    private List<User> users;

    private String roomer;

    private MahjongManager manager;

    public Room() {
    }

    public void init(Integer roomId) {
        this.roomId = roomId;
        this.roomPublic = true;
        this.roomer = null;
        users = Lists.newArrayList();
        manager = new MahjongManager();
    }


}
