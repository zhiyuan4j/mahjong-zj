package red.zhiyuan.mahjongzj.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import red.zhiyuan.mahjongzj.bean.JsonReturn;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
@RestController
public class IndexController {

    @RequestMapping("/connect")
    public JsonReturn connect() {
        return JsonReturn.success(null, "TODO");
    }
}
