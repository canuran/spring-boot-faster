package canuran.faster.application.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import canuran.common.utils.StringDateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
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
 * @author canuran
 */
@Configuration
public class WebAppConfigurer extends WebMvcConfigurerAdapter {

    @Autowired(required = false)
    private AbstractJackson2HttpMessageConverter converter;

    /**
     * 注册Jackson配置。
     */
    @PostConstruct
    public void registerJsonModule() {
        if (converter == null) {
            return;
        }
        SimpleModule simpleModule = new SimpleModule();
        // 大数字用字符串代替科学计数法
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
        // 大数字用字符串代替科学计数法
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
        // 大数字用字符串代替科学计数法
        simpleModule.addSerializer(Long.class, new JsonSerializer<Long>() {
            @Override
            public void serialize(Long aLong, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                if (aLong == null) {
                    jsonGenerator.writeNull();
                } else {
                    jsonGenerator.writeString(aLong.toString());
                }
            }
        });
        // 支持序列化多种格式的Date
        simpleModule.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date date, JsonGenerator jsonGenerator,
                                  SerializerProvider serializerProvider) throws IOException {
                if (date == null) {
                    jsonGenerator.writeNull();
                } else {
                    jsonGenerator.writeString(StringDateParser.getDateTimeString(date));
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

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                return StringDateParser.stringToDate(source);
            }
        });
        registry.addConverter(new Converter<String, java.sql.Date>() {
            @Override
            public java.sql.Date convert(String source) {
                return StringDateParser.stringToSqlDate(source);
            }
        });
        registry.addConverter(new Converter<String, Timestamp>() {
            @Override
            public Timestamp convert(String source) {
                return StringDateParser.stringToTimestamp(source);
            }
        });
        registry.addConverter(new Converter<Timestamp, String>() {
            @Override
            public String convert(Timestamp source) {
                return StringDateParser.getTimestampString(source);
            }
        });
        registry.addConverter(new Converter<java.sql.Date, String>() {
            @Override
            public String convert(java.sql.Date source) {
                return StringDateParser.getDateString(source);
            }
        });
        registry.addConverter(new Converter<Date, String>() {
            @Override
            public String convert(Date source) {
                return StringDateParser.getDateTimeString(source);
            }
        });
        super.addFormatters(registry);
    }

}