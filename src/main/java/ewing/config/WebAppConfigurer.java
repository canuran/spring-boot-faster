package ewing.config;

import ewing.common.StringDateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class WebAppConfigurer extends WebMvcConfigurerAdapter {

    public static Logger logger = LoggerFactory.getLogger(WebAppConfigurer.class);

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        super.addViewControllers(registry);
    }

    /**
     * 字符串转换成日期。
     */
    @Component
    public static class StringToDate implements Converter<String, Date> {
        @Override
        public Date convert(String source) {
            return StringDateParser.stringToDate(source);
        }
    }

    /**
     * 字符串转换成Timestamp。
     */
    @Component
    public static class StringToTimestamp implements Converter<String, Timestamp> {
        @Override
        public Timestamp convert(String source) {
            return StringDateParser.stringToTimestamp(source);
        }
    }

    /**
     * 日期转换成字符串。
     */
    @Component
    public static class DateToString implements Converter<Date, String> {
        @Override
        public String convert(Date source) {
            if (source == null) {
                return null;
            }
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(source);
        }
    }

    /**
     * 国际化资源绑定到容器和请求上下文，使用Header中的Accept-Language。
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setBasename("messages/message");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}