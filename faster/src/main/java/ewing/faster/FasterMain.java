package ewing.faster;

import ewing.common.config.ExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class FasterMain {

    public static final long START_TIME = System.currentTimeMillis();

    public static void main(String[] args) {
        SpringApplication.run(FasterMain.class, args);
    }

    @Bean
    public ExceptionHandler exceptionHandler() {
        return new ExceptionHandler(FasterMain.class.getPackage().getName());
    }

}
