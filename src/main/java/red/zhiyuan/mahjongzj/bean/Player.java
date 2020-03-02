package red.zhiyuan.mahjongzj.bean;

import lombok.Data;
import red.zhiyuan.mahjongzj.model.BaseMahjong;

import javax.websocket.Session;
import java.util.List;
import java.util.Objects;

/**
 * @author zhiyuan.wang
 * @date 2020/2/1
 */
@Data
public class Player {

    private String id;

    private List<BaseMahjong> privateMahjongs;

    private List<List<BaseMahjong>> publicMahjongs;

    private List<BaseMahjong> firedMahjongs;

    private Session session;

    private Boolean peng;

    private Boolean gang;

    private Boolean hu;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
