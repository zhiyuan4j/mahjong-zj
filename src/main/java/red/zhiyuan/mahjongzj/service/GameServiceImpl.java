package red.zhiyuan.mahjongzj.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import red.zhiyuan.mahjongzj.bean.JsonReturn;
import red.zhiyuan.mahjongzj.bean.MahjongManager;
import red.zhiyuan.mahjongzj.bean.Player;
import red.zhiyuan.mahjongzj.bean.Room;
import red.zhiyuan.mahjongzj.enums.UserRequestType;
import red.zhiyuan.mahjongzj.enums.UserResponseType;
import red.zhiyuan.mahjongzj.model.BaseMahjong;
import red.zhiyuan.mahjongzj.model.User;
import red.zhiyuan.mahjongzj.util.MahjongUtil;
import red.zhiyuan.mahjongzj.util.MessageUtil;
import red.zhiyuan.mahjongzj.util.RoomConvertUtil;
import red.zhiyuan.mahjongzj.util.SessionUtil;
import red.zhiyuan.mahjongzj.vo.*;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhiyuan.wang
 * @date 2020/2/1
 */
@Service
public class GameServiceImpl implements IGameService {

    private Map<String, User> userMap = Maps.newHashMap();

    private Map<Integer, Room> roomMap = Maps.newHashMap();

    private Map<String, Integer> userRoomMap = Maps.newHashMap();

    @Override
    public void connect(String userId, Session session) {

        if (StringUtils.isBlank(userId)) {
            MessageUtil.send(JsonReturn.error(UserResponseType.CONNECT.name(), "用户昵称不能为空"), session);
        }

        if (StringUtils.isBlank(userId) || userMap.containsKey(userId)) {
            MessageUtil.send(JsonReturn.error(UserResponseType.CONNECT.name(), "用户已存在"), session);
        }

        boolean addResult = SessionUtil.add(userId, session);

        if (addResult) {
            User user = new User();
            user.setId(userId);
            userMap.put(userId, user);
            MessageUtil.send(JsonReturn.success(UserResponseType.CONNECT.name(), userId + "连接成功"), session);
        } else {
            MessageUtil.send(JsonReturn.error(UserResponseType.CONNECT.name(), "用户的会话已存在"), session);
        }

    }

    @Override
    public void disconnect(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        quitRoom(userId);
        userMap.remove(userId);
        SessionUtil.remove(userId);
    }

    @Override
    public void getRooms(String userId) {
        Session session = SessionUtil.get(userId);
        if (session == null) {
            return;
        }
        List<RoomVO> rooms = roomMap.values().stream()
                .filter(Room::getRoomPublic)
                .map(RoomConvertUtil::concert2VO)
                .collect(Collectors.toList());
        MessageUtil.send(JsonReturn.success(UserResponseType.ROOM_LIST.name(), rooms), session);
    }

    @Override
    public void createRoom(String userId) {

        Session session = SessionUtil.get(userId);
        if (session == null) {
            return;
        }

        if (StringUtils.isBlank(userId)) {
            MessageUtil.send(JsonReturn.error(UserResponseType.CREATE_ROOM.name(), "非法请求"), session);
        }

        if (userRoomMap.containsKey(userId)) {
            MessageUtil.send(JsonReturn.error(UserResponseType.CREATE_ROOM.name(), "您已在房间中, 无法创建新房间"), session);
        }

        Integer roomId = null;
        do {
            roomId = getRandomRoomId();
        } while (roomMap.containsKey(roomId));

        Room room = new Room();
        room.init(roomId);

        roomMap.put(roomId, room);

        MessageUtil.send(JsonReturn.success(UserResponseType.CREATE_ROOM.name(), roomId), session);

    }

    @Override
    public void joinRoom(String userId, Integer roomId) {

        Session session = SessionUtil.get(userId);
        if (session == null) {
            return;
        }

        if (StringUtils.isBlank(userId) || roomId == null) {
            MessageUtil.send(JsonReturn.error(UserResponseType.JOIN_ROOM.name(), "非法请求"), session);
        }

        if (!userMap.containsKey(userId)) {
            MessageUtil.send(JsonReturn.error(UserResponseType.JOIN_ROOM.name(),"用户" + userId + "不存在"), session);
        }

        if (!roomMap.containsKey(roomId)) {
            MessageUtil.send(JsonReturn.error(UserResponseType.JOIN_ROOM.name(),"房间" + roomId + "不存在"), session);
        }

        Integer existRoom = userRoomMap.get(userId);

        if (existRoom != null) {
            MessageUtil.send(JsonReturn.error(UserResponseType.JOIN_ROOM.name(),"用户" + userId + "已加入房间:" + existRoom), session);
        } else {
            userRoomMap.put(userId, roomId);
            Room room = roomMap.get(roomId);
            room.getUsers().add(userMap.get(userId));

            if (room.getUsers().size() == 1 || room.getRoomId() == null) {
                room.setRoomer(userId);
            }


            room.getUsers().forEach(u -> {
                GameVO gameVO = new GameVO();
                gameVO.setRoomPublic(room.getRoomPublic().toString());
                gameVO.setRoomId(roomId);
                gameVO.setRoomer(room.getRoomer());
                gameVO.setPlayers(room.getUsers().stream().map(User::getId).collect(Collectors.toList()));
                gameVO.setMyId(u.getId());
                Session current = SessionUtil.get(u.getId());
                if (current != null) {
                    MessageUtil.send(JsonReturn.success(UserResponseType.JOIN_ROOM.name(), gameVO), current);
                }
            });
        }

    }

    @Override
    public void quitRoom(String userId) {

        if (StringUtils.isBlank(userId)) {
            return;
        }
        User user = userMap.get(userId);
        if (user == null) {
            return;
        }
        Integer roomId = userRoomMap.get(userId);
        if (roomId == null) {
            return;
        }

        roomMap.get(roomId).getUsers().remove(user);
        userRoomMap.remove(userId);

        if (CollectionUtils.isEmpty(roomMap.get(roomId).getUsers())) {
            roomMap.remove(roomId);
        } else {
            Room room = roomMap.get(roomId);
            room.setRoomer(room.getUsers().get(0).getId());
            roomMap.get(roomId).getUsers().forEach(u -> {
                GameVO gameVO = new GameVO();
                gameVO.setRoomPublic(room.getRoomPublic().toString());
                gameVO.setRoomId(roomId);
                gameVO.setPlayers(room.getUsers().stream().map(User::getId).collect(Collectors.toList()));
                gameVO.setMyId(u.getId());
                gameVO.setRoomer(room.getRoomer());
                Session current = SessionUtil.get(u.getId());
                if (current != null) {
                    MessageUtil.send(JsonReturn.success(UserResponseType.JOIN_ROOM.name(),gameVO), current);
                }
            });
        }

    }

    @Override
    public void startGame(Integer roomId) {
        if (roomId == null) {
            return;
        }
        Room room = roomMap.get(roomId);
        if (room == null) {
            return;
        }
        List<Player> players = room.getUsers().stream().map(this::buildPlayer).collect(Collectors.toList());

        room.getManager().startGame(players);

        for (Player player : players) {

            UserGameVO userGameVO = new UserGameVO();
            userGameVO.setPeng(false);
            userGameVO.setGang(false);
            userGameVO.setHu(false);
            userGameVO.setHasOperation(false);
            userGameVO.setMyOperation(false);
            if (player.getId().equals(room.getManager().getTurnUser())) {
                userGameVO.setMyTurn(true);
            } else {
                userGameVO.setMyTurn(false);
            }
            userGameVO.setPrivateMahjongs(player.getPrivateMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            userGameVO.setUserPublics(Lists.newArrayList());
            userGameVO.setUserId(player.getId());
            userGameVO.setLeftMahjongCount(room.getManager().getAllMahjongs().size() - room.getManager().getNextDispatchIndex());
            userGameVO.setBaida(buildMahjongVO(room.getManager().getBaida()));
            userGameVO.setFiredMahjongs(room.getManager().getFiredMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            MessageUtil.send(JsonReturn.success(UserResponseType.START_GAME.name(), userGameVO), player.getSession());
        }

    }

    private Player buildPlayer(User user) {
        Player player = new Player();
        player.setFiredMahjongs(Lists.newArrayList());
        player.setId(user.getId());
        player.setPrivateMahjongs(Lists.newArrayList());
        player.setPublicMahjongs(Lists.newArrayList());
        player.setSession(SessionUtil.get(user.getId()));
        return player;
    }

    @Override
    public void dispatch(String userId) {
        if (userId == null) {
            return;
        }

        Integer roomId = userRoomMap.get(userId);

        if (roomId == null) {
            return;
        }

        Room room = roomMap.get(roomId);

        if (room == null) {
            return;
        }

        BaseMahjong dispatch = room.getManager().dispatch(userId);

        room.getManager().getPlayers().forEach(p -> {

            UserGameVO userGameVO = new UserGameVO();
            userGameVO.setPeng(p.getPeng());
            userGameVO.setGang(p.getGang());
            userGameVO.setHu(p.getHu());
            if (p.getId().equals(room.getManager().getTurnUser())) {
                userGameVO.setDispatch(buildMahjongVO(dispatch));
                userGameVO.setMyTurn(true);
            } else {
                userGameVO.setMyTurn(false);
            }
            userGameVO.setHasOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()));
            userGameVO.setMyOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()) && room.getManager().getOperationUsers().get(0).equals(p.getId()));
            userGameVO.setPrivateMahjongs(p.getPrivateMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            userGameVO.setUserPublics(buildUserVO(room.getManager()));
            userGameVO.setUserId(p.getId());
            userGameVO.setLeftMahjongCount(room.getManager().getAllMahjongs().size() - room.getManager().getNextDispatchIndex());
            userGameVO.setBaida(buildMahjongVO(room.getManager().getBaida()));
            userGameVO.setFiredMahjongs(room.getManager().getFiredMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            MessageUtil.send(JsonReturn.success(UserResponseType.DISPATCH.name(), userGameVO), p.getSession());
        });

    }

    @Override
    public void fire(String userId, String fired) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fired)) {
            return;
        }

        Integer roomId = userRoomMap.get(userId);
        Room room = roomMap.get(roomId);
        room.getManager().fire(userId, fired);

        room.getManager().getPlayers().forEach(p -> {

            UserGameVO userGameVO = new UserGameVO();
            userGameVO.setPeng(p.getPeng());
            userGameVO.setGang(p.getGang());
            userGameVO.setHu(p.getHu());
            userGameVO.setDispatch(null);
            if (p.getId().equals(room.getManager().getTurnUser())) {
                userGameVO.setMyTurn(true);
            } else {
                userGameVO.setMyTurn(false);
            }
            userGameVO.setHasOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()));
            userGameVO.setMyOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()) && room.getManager().getOperationUsers().get(0).equals(p.getId()));
            userGameVO.setPrivateMahjongs(p.getPrivateMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            userGameVO.setUserPublics(buildUserVO(room.getManager()));
            userGameVO.setUserId(p.getId());
            userGameVO.setLeftMahjongCount(room.getManager().getAllMahjongs().size() - room.getManager().getNextDispatchIndex());
            userGameVO.setBaida(buildMahjongVO(room.getManager().getBaida()));
            userGameVO.setFiredMahjongs(room.getManager().getFiredMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            MessageUtil.send(JsonReturn.success(UserResponseType.FIRE.name(), userGameVO), p.getSession());
        });
    }

    @Override
    public void peng(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }

        Integer roomId = userRoomMap.get(userId);
        Room room = roomMap.get(roomId);
        room.getManager().peng();

        room.getManager().getPlayers().forEach(p -> {

            UserGameVO userGameVO = new UserGameVO();
            userGameVO.setPeng(false);
            userGameVO.setGang(false);
            userGameVO.setHu(false);
            userGameVO.setDispatch(null);
            if (p.getId().equals(room.getManager().getTurnUser())) {
                userGameVO.setMyTurn(true);
            } else {
                userGameVO.setMyTurn(false);
            }
            userGameVO.setHasOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()));
            userGameVO.setMyOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()) && room.getManager().getOperationUsers().get(0).equals(p.getId()));
            userGameVO.setPrivateMahjongs(p.getPrivateMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            userGameVO.setUserPublics(buildUserVO(room.getManager()));
            userGameVO.setUserId(p.getId());
            userGameVO.setLeftMahjongCount(room.getManager().getAllMahjongs().size() - room.getManager().getNextDispatchIndex());
            userGameVO.setBaida(buildMahjongVO(room.getManager().getBaida()));
            userGameVO.setFiredMahjongs(room.getManager().getFiredMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            MessageUtil.send(JsonReturn.success(UserResponseType.PENG.name(), userGameVO), p.getSession());
        });
    }

    @Override
    public void gang(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }

        Integer roomId = userRoomMap.get(userId);
        Room room = roomMap.get(roomId);
        room.getManager().gang();

        room.getManager().getPlayers().forEach(p -> {

            UserGameVO userGameVO = new UserGameVO();
            userGameVO.setPeng(false);
            userGameVO.setGang(false);
            userGameVO.setHu(false);
            userGameVO.setDispatch(null);
            if (p.getId().equals(room.getManager().getTurnUser())) {
                userGameVO.setMyTurn(true);
            } else {
                userGameVO.setMyTurn(false);
            }
            userGameVO.setHasOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()));
            userGameVO.setMyOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()) && room.getManager().getOperationUsers().get(0).equals(p.getId()));
            userGameVO.setPrivateMahjongs(p.getPrivateMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            userGameVO.setUserPublics(buildUserVO(room.getManager()));
            userGameVO.setUserId(p.getId());
            userGameVO.setLeftMahjongCount(room.getManager().getAllMahjongs().size() - room.getManager().getNextDispatchIndex());
            userGameVO.setBaida(buildMahjongVO(room.getManager().getBaida()));
            userGameVO.setFiredMahjongs(room.getManager().getFiredMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            MessageUtil.send(JsonReturn.success(UserResponseType.GANG.name(), userGameVO), p.getSession());
        });
    }

    @Override
    public void hu(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }

        Integer roomId = userRoomMap.get(userId);
        Room room = roomMap.get(roomId);
        List<List<BaseMahjong>> huList = room.getManager().hu();
        room.getManager().getPlayers().forEach(p -> {
            UserGameVO userGameVO = new UserGameVO();
            userGameVO.setUserHu(buildUserHu(huList, userId));
            MessageUtil.send(JsonReturn.success(UserResponseType.HU.name(), userGameVO), p.getSession());
        });
    }

    private HuVO buildUserHu(List<List<BaseMahjong>> huList, String userId) {
        HuVO vo = new HuVO();
        vo.setBanker(userId);
        vo.setHu(userId);
        vo.setMahjongs(huList.stream().flatMap(List::stream).map(this::buildMahjongVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public void giveUp(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }

        Integer roomId = userRoomMap.get(userId);
        Room room = roomMap.get(roomId);
        room.getManager().giveUp(userId);

        room.getManager().getPlayers().forEach(p -> {

            UserGameVO userGameVO = new UserGameVO();
            userGameVO.setPeng(false);
            userGameVO.setGang(false);
            userGameVO.setHu(false);
            userGameVO.setDispatch(null);
            if (p.getId().equals(room.getManager().getTurnUser())) {
                userGameVO.setMyTurn(true);
            } else {
                userGameVO.setMyTurn(false);
            }
            userGameVO.setHasOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()));
            userGameVO.setMyOperation(!CollectionUtils.isEmpty(room.getManager().getOperationUsers()) && room.getManager().getOperationUsers().get(0).equals(p.getId()));
            userGameVO.setPrivateMahjongs(p.getPrivateMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            userGameVO.setUserPublics(buildUserVO(room.getManager()));
            userGameVO.setUserId(p.getId());
            userGameVO.setLeftMahjongCount(room.getManager().getAllMahjongs().size() - room.getManager().getNextDispatchIndex());
            userGameVO.setBaida(buildMahjongVO(room.getManager().getBaida()));
            userGameVO.setFiredMahjongs(room.getManager().getFiredMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            MessageUtil.send(JsonReturn.success(UserResponseType.GANG.name(), userGameVO), p.getSession());
        });
    }

    private List<UserVO> buildUserVO(MahjongManager manager) {
        return manager.getPlayers().stream().map(p -> {
            UserVO userVO = new UserVO();
            userVO.setFiredMahjongs(p.getFiredMahjongs().stream().map(this::buildMahjongVO).collect(Collectors.toList()));
            userVO.setPublicMahjongs(p.getPublicMahjongs().stream().map(list -> list.stream().map(this::buildMahjongVO).collect(Collectors.toList())).collect(Collectors.toList()));
            userVO.setUserId(p.getId());
            return userVO;
        }).collect(Collectors.toList());
    }

    private Integer getRandomRoomId() {
        return RandomUtils.nextInt(1, 1000);
    }

    private MahjongVO buildMahjongVO(BaseMahjong mahjong) {
        MahjongVO vo = new MahjongVO();
        vo.setName(mahjong.toString());
        vo.setType(mahjong.getType());
        return vo;
    }
}
