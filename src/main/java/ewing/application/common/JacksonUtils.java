package ewing.application.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Jackson简单封装。
 *
 * @author Ewing
 */
public class JacksonUtils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
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
        OBJECT_MAPPER.registerModule(simpleModule);
    }

    /**
     * 把Java对象实例转换为Json字符串。
     *
     * @param object Java对象实例
     * @return Json字符串
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 把Json字符串转换为复杂的Java对象或集合。
     *
     * @param json 原始Json字符串数据
     * @param type 转成该参数上的泛型类型
     * @return Java对象实例
     */
    public static <T> T toObject(String json, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 把Json字符串转换为相应的JavaBean对象。
     *
     * @param json 原始Json字符串数据
     * @param type 要转换的Java对象类型
     * @return Java对象实例
     */
    public static <T> T toObject(String json, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成用于延迟日志打印的对象。
     */
    public static Wrapper wrap(Object source) {
        return new Wrapper(source);
    }

    /**
     * 用于延迟到需要打印日志时才转成JSON。
     */
    public static class Wrapper {

        private Object source;

        Wrapper(Object source) {
            this.source = source;
        }

        @Override
        public String toString() {
            try {
                return OBJECT_MAPPER.writeValueAsString(source);
            } catch (JsonProcessingException e) {
                return String.valueOf(source);
            }
        }
    }

}