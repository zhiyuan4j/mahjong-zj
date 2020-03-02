package red.zhiyuan.mahjongzj.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhiyuan.wang
 * @date 2020/2/2
 */
@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }

    public static <T> T getBean(Class<T> type) {
        T bean = null;

        Map<String, T> map = applicationContext.getBeansOfType(type);
        if (map.size() == 1) {
            // only return the bean if there is exactly one
            bean = (T) map.values().iterator().next();
        }
        return bean;
    }
}
