package ewing.application.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ewing.application.common.AliasName;
import ewing.application.common.StringDateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Spring MVC 全局配置。
 *
 * @author Ewing
 */
@Configuration
public class WebAppConfigurer extends WebMvcConfigurerAdapter {

    @Autowired
    private MappingJackson2HttpMessageConverter converter;

    /**
     * 注册Jackson配置。
     */
    @PostConstruct
    public void registerJsonModule() {
        SimpleModule simpleModule = new SimpleModule();
        // 大数字用字符串表示，避免返回科学计数法
        simpleModule.addSerializer(Number.class, new JsonSerializer<Number>() {
            @Override
            public void serialize(Number number, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                if (number instanceof BigInteger || number instanceof BigDecimal) {
                    jsonGenerator.writeString(number.toString());
                } else if (number == null) {
                    jsonGenerator.writeNull();
                } else {
                    jsonGenerator.writeNumber(number.toString());
                }
            }
        });
        // 把实现了有别名的接口的属性添加别名，用于前端显示
        simpleModule.addSerializer(AliasName.class, new JsonSerializer<AliasName>() {
            @Override
            public void serialize(AliasName source, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                if (source == null) {
                    jsonGenerator.writeNull();
                } else {
                    jsonGenerator.writeObject(source.name());
                    JsonStreamContext context = jsonGenerator.getOutputContext();
                    if (context.inObject()) {
                        String fieldName = context.getCurrentName();
                        jsonGenerator.writeStringField(fieldName + "Alias", source.alias());
                    }
                }
            }
        });
        converter.getObjectMapper().registerModule(simpleModule);
    }

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