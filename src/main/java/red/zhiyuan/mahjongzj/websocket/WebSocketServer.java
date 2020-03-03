package red.zhiyuan.mahjongzj.websocket;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import red.zhiyuan.mahjongzj.bean.JsonReturn;
import red.zhiyuan.mahjongzj.enums.UserRequestType;
import red.zhiyuan.mahjongzj.enums.UserResponseType;
import red.zhiyuan.mahjongzj.service.IGameService;
import red.zhiyuan.mahjongzj.util.BeanUtil;
import red.zhiyuan.mahjongzj.util.MessageUtil;
import red.zhiyuan.mahjongzj.util.SessionUtil;
import red.zhiyuan.mahjongzj.vo.UserRequest;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/mahjong/{userId}")
@Component
public class WebSocketServer {

    private static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    private IGameService gameService = BeanUtil.getBean(IGameService.class);

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {

        log.info("处理用户连接:" + userId);

        gameService.connect(userId, session);

    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId")String userId, Session session, CloseReason closeReason) {
        log.info("用户退出连接:" + userId + ", 原因:" + JSONObject.toJSONString(closeReason));
        gameService.disconnect(userId);
    }

    @OnMessage
    public void onMessage(@PathParam("userId") String userId, String message, Session session) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        UserRequest request = getRequest(message);

        String type = request.getType();
        if (UserRequestType.ROOM_LIST.name().equals(type)) {
            gameService.getRooms(userId);
        } else if (UserRequestType.CREATE_ROOM.name().equals(type)) {
            gameService.createRoom(userId);
        } else if (UserRequestType.JOIN_ROOM.name().equals(type)) {
            gameService.joinRoom(userId, request.getRoomId());
        } else if (UserRequestType.QUIT_ROOM.name().equals(type)) {
            gameService.quitRoom(userId);
        } else if (UserResponseType.START_GAME.name().equals(type)) {
            gameService.startGame(request.getRoomId());
        } else if (UserResponseType.DISPATCH.name().equals(type)) {
            gameService.dispatch(userId);
        } else if (UserResponseType.FIRE.name().equals(type)) {
            gameService.fire(userId, request.getMahjong());
        } else if (UserResponseType.PENG.name().equals(type)) {
            gameService.peng(userId);
        } else if (UserResponseType.GANG.name().equals(type)) {
            gameService.gang(userId);
        } else if (UserResponseType.HU.name().equals(type)) {
            gameService.hu(userId);
        } else if (UserResponseType.GIVE_UP.name().equals(type)) {
            gameService.giveUp(userId);
        }

    }

    @OnError
    public void onError(@PathParam("userId")String userId, Session session, Throwable error) {
        log.error("用户连接错误:" + userId + ", 原因:"+error.getMessage());
    }

    private UserRequest getRequest(String message) {
        try {
            return JSONObject.parseObject(message, UserRequest.class);
        } catch (RuntimeException e) {
            log.error("fail to parse request, requestMessage = " + message);
            return null;
        }
    }

}