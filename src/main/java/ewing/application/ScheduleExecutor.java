package ewing.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定时任务测试。
 *
 * @author Ewing
 * @date 2017/4/24
 */
@Component
public class ScheduleExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleExecutor.class);

    /**
     * 执行定时任务，执行频率单位为毫秒。
     */
    @Scheduled(fixedRate = 600000)
    public void nowTime() {
        LOGGER.info("现在是北京时间：" + new SimpleDateFormat("y-M-d HH:mm:ss").format(new Date()));
    }

}
