package red.zhiyuan.mahjongzj.service;

import red.zhiyuan.mahjongzj.bean.JsonReturn;
import red.zhiyuan.mahjongzj.bean.Room;

import javax.websocket.Session;
import java.util.List;

/**
 * @author zhiyuan.wang
 * @date 2020/2/1
 */
public interface IGameService {

    void connect(String userId, Session session);

    void disconnect(String userId);

    void getRooms(String userId);

    void createRoom(String userId);

    void joinRoom(String userId, Integer roomId);

    void quitRoom(String userId);

    void startGame(Integer roomId);

    void dispatch(String userId);

    void fire(String userId, String fired);

    void peng();

    void gang();

    void hu();

    void giveUp();

}
