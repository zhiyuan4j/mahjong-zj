package red.zhiyuan.mahjongzj.util;

import red.zhiyuan.mahjongzj.bean.Room;
import red.zhiyuan.mahjongzj.vo.RoomVO;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
public class RoomConvertUtil {
    public static RoomVO concert2VO(Room room) {
        if (room == null) {
            return null;
        }
        RoomVO vo = new RoomVO();
        vo.setRoomId(room.getRoomId());
        vo.setPlayerCount(room.getUsers().size());
        return vo;
    }
}
