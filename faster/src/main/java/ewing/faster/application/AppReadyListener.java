package ewing.faster.application;

import ewing.faster.FasterMain;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 应用程序事件监听器，此处监听启动完成事件。
 *
 * @author Ewing
 */
@Component
public class AppReadyListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        System.out.println("应用启动成功！启动用时：" + (System.currentTimeMillis() - FasterMain.START_TIME) + " 毫秒。");
    }

}