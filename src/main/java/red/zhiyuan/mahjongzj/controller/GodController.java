package red.zhiyuan.mahjongzj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import red.zhiyuan.mahjongzj.bean.JsonReturn;
import red.zhiyuan.mahjongzj.service.IGameService;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
@Controller
@RequestMapping("/god")
public class GodController {

    @Autowired
    private IGameService gameService;

    @RequestMapping("/reset")
    @ResponseBody
    public String index(Integer roomId) {
        gameService.startGame(roomId);
        return "OK";
    }
}
