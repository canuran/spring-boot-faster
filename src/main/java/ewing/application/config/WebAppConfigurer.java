package ewing.application.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ewing.application.common.StringDateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
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
        simpleModule.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal decimal, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                if (decimal == null) {
                    jsonGenerator.writeNull();
                } else {
                    jsonGenerator.writeString(decimal.stripTrailingZeros().toPlainString());
                }
            }
        });
        // 大数字用字符串表示，避免返回科学计数法
        simpleModule.addSerializer(BigInteger.class, new JsonSerializer<BigInteger>() {
            @Override
            public void serialize(BigInteger bigInteger, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                if (bigInteger == null) {
                    jsonGenerator.writeNull();
                } else {
                    jsonGenerator.writeString(bigInteger.toString());
                }
            }
        });
        // 支持反序列化多种格式的Date
        simpleModule.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                String value = jsonParser.getValueAsString();
                return StringDateParser.stringToDate(value);
            }
        });
        // 支持反序列化多种格式的java.sql.Date
        simpleModule.addDeserializer(java.sql.Date.class, new JsonDeserializer<java.sql.Date>() {
            @Override
            public java.sql.Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                String value = jsonParser.getValueAsString();
                return StringDateParser.stringToSqlDate(value);
            }
        });
        converter.getObjectMapper().registerModule(simpleModule);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/login.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
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
            return StringDateParser.dateToString(source);
        }
    }

}