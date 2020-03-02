package red.zhiyuan.mahjongzj.bean;

import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import red.zhiyuan.mahjongzj.model.BaseMahjong;
import red.zhiyuan.mahjongzj.model.Dice;
import red.zhiyuan.mahjongzj.struct.CycleLink;
import red.zhiyuan.mahjongzj.util.MahjongUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhiyuan.wang
 * @date 2020/1/26
 */
@Component
@Data
public class MahjongManager {

    private List<BaseMahjong> allMahjongs;

    private int nextDispatchIndex;

    private BaseMahjong currentFiredMahjong;

    private CycleLink<Player> playerLink;

    private List<Dice> dices;

    private BaseMahjong fakeBaida;

    private BaseMahjong baida;

    private List<BaseMahjong> firedMahjongs;

    private List<String> operationUsers;

    public void init() {
        allMahjongs = MahjongUtil.createZhenJiangMahjongs();
        nextDispatchIndex = 0;
        currentFiredMahjong = null;
        playerLink = new CycleLink<>();
        dices = Lists.newArrayList(new Dice(), new Dice());
        firedMahjongs = Lists.newArrayList();
        operationUsers = Lists.newArrayList();
    }

    // 开局摸牌
    public void startGame(List<Player> players) {

        init();

        players.forEach(playerLink::add);

        dices.forEach(dice -> dice.setNumber(RandomUtils.nextInt(1, 7)));

        Collections.shuffle(allMahjongs);

        for (int times=0; times<3; times++) {
            for (Player player : players) {
                player.getPrivateMahjongs().add(allMahjongs.get(nextDispatchIndex));
                nextDispatchIndex++;
                player.getPrivateMahjongs().add(allMahjongs.get(nextDispatchIndex));
                nextDispatchIndex++;
                player.getPrivateMahjongs().add(allMahjongs.get(nextDispatchIndex));
                nextDispatchIndex++;
                player.getPrivateMahjongs().add(allMahjongs.get(nextDispatchIndex));
                nextDispatchIndex++;
            }
        }

        // 单抓
        for (Player player : players) {
            player.getPrivateMahjongs().add(allMahjongs.get(nextDispatchIndex));
            nextDispatchIndex++;
        }

        // 手牌整理
        for (Player player : players) {
            MahjongUtil.sortMohjongs(player.getPrivateMahjongs());
        }

        // 翻百搭
        fakeBaida = allMahjongs.get(nextDispatchIndex);
        nextDispatchIndex++;
        firedMahjongs.add(fakeBaida);
        baida = getBaida(fakeBaida);

    }

    // 摸牌
    public BaseMahjong dispatch(String userId) {
        BaseMahjong baseMahjong = allMahjongs.get(nextDispatchIndex);
        nextDispatchIndex++;
        List<BaseMahjong> privateMahjongs = getPlayerInLink(userId).getPrivateMahjongs();
        privateMahjongs.add(baseMahjong);
        if (MahjongUtil.hasAnGang(privateMahjongs)) {
            operationUsers.add(userId);
        } else {
            operationUsers = Lists.newArrayList();
        }
        getPlayers().forEach(p -> {
            p.setPeng(false);
            p.setGang(MahjongUtil.hasAnGang(p.getPrivateMahjongs()));
            p.setHu(MahjongUtil.hasHu(p.getPrivateMahjongs(), baida.toString()));
        });
        return baseMahjong;
    }

    public Player getPlayerInLink(String userId) {
        Player player = new Player();
        player.setId(userId);
        int index = playerLink.getIndex(player);
        return playerLink.getElement(index);
    }

    public Player getNextPlayer(String userId) {
        Player player = new Player();
        player.setId(userId);
        return playerLink.nextElement(player);
    }

    public List<Player> getPlayers() {
        if (playerLink == null) {
            return Lists.newArrayList();
        }
        return playerLink.toList().stream().map(node -> (Player)(node.data)).collect(Collectors.toList());
    }

    // 打牌
    public void fire(String userId, String fired) {
        Player player = getPlayerInLink(userId);
        BaseMahjong firedMahjong = null;
        for (BaseMahjong mahjong : player.getPrivateMahjongs()) {
            if (mahjong.toString().equals(fired)) {
                firedMahjong = mahjong;
                break;
            }
        }
        player.getPrivateMahjongs().remove(firedMahjong);
        firedMahjongs.add(firedMahjong);


        getPlayers().forEach(p -> {

            MahjongUtil.sortMohjongs(p.getPrivateMahjongs());

            if (p.getId().equals(userId)) {
                p.setPeng(false);
                p.setGang(false);
                p.setHu(false);
            } else {
                p.setPeng(MahjongUtil.hasPeng(fired, p.getPrivateMahjongs()));
                p.setGang(MahjongUtil.hasAnGang(p.getPrivateMahjongs()));
                p.setHu(MahjongUtil.hasHu(p.getPrivateMahjongs(), baida.toString()));
            }
        });

        operationUsers = Lists.newArrayList();
        addHuByOrder(userId);
        addPengOrGangByOrder(userId);
    }

    private void addPengOrGangByOrder(String userId) {
        Player currentPlayer = getPlayerInLink(userId);
        for (int i=0; i<getPlayers().size() - 1; i++) {
            Player next = playerLink.nextElement(currentPlayer);
            if ((next.getPeng() != null && next.getPeng())
                    || (next.getGang() != null && next.getGang())) {
                if (!operationUsers.contains(next.getId())) {
                    operationUsers.add(next.getId());
                }
            } else {
                currentPlayer = next;
            }
        }
    }

    private void addHuByOrder(String userId) {
        Player currentPlayer = getPlayerInLink(userId);
        for (int i=0; i<getPlayers().size() - 1; i++) {
            Player next = playerLink.nextElement(currentPlayer);
            if (next.getHu() !=null && next.getHu()) {
                operationUsers.add(next.getId());
            } else {
                currentPlayer = next;
            }
        }
    }

    private BaseMahjong getBaida(BaseMahjong baseMahjong) {
        int index = MahjongUtil.numberMahjongLink.getIndex(baseMahjong);
        if (baseMahjong.getType() <= 3) {
            return MahjongUtil.numberMahjongLink.getNodeByIndex(index).next.data;
        }
        if (baseMahjong.getType() >= 4 && baseMahjong.getType() <= 7) {
            return MahjongUtil.fengMahjongLink.getNodeByIndex(index).next.data;
        }
        return MahjongUtil.specialMahjongLink.getNodeByIndex(index).next.data;
    }

}
