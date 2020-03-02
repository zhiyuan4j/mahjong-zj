package red.zhiyuan.mahjongzj.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.Session;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
public class SessionUtil {

    private static ConcurrentMap<String, Session> userSessionMap = Maps.newConcurrentMap();

    public static boolean add(String userId, Session session) {
        if (StringUtils.isBlank(userId)) {
            return false;
        }
        if (userSessionMap.containsKey(userId)) {
            return false;
        } else {
            userSessionMap.put(userId, session);
            return true;
        }
    }

    public static void remove(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        userSessionMap.remove(userId);
    }

    public static Session get(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        return userSessionMap.get(userId);
    }

}
