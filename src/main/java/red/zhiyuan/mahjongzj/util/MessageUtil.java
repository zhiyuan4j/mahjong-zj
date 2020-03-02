package red.zhiyuan.mahjongzj.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import red.zhiyuan.mahjongzj.bean.JsonReturn;
import red.zhiyuan.mahjongzj.exception.ServerException;

import javax.websocket.Session;
import java.io.IOException;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
@Slf4j
public class MessageUtil {

    public static void send(JsonReturn response, Session session) {
        if (session == null) {
            throw new ServerException("invalid session");
        }
        String message = JSONObject.toJSONString(response);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("fail to send message, message=" + message, e);
        }
    }

    public static void send(JsonReturn response, String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new ServerException("invalid userId");
        }
        Session session = SessionUtil.get(userId);
        if (session == null) {
            log.warn("fail to send message, userId=" + userId + " may have already disconnected");
            return;
        }
        String message = JSONObject.toJSONString(response);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("fail to send message, message=" + message + " userId=" + userId, e);
        }
    }

}
