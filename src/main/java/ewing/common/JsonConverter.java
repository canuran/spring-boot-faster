package ewing.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.text.SimpleDateFormat;

public class JsonConverter {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 把Json字符串转换为相应的Java对象。
     *
     * @param json 要转换的Java对象类型
     * @param type 原始Json字符串数据
     * @return Java对象实例
     */
    public static <T> T toObject(String json, Class<T> type) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 把Java对象实例转换为Json字符串。
     *
     * @param object Java对象实例
     * @return Json字符串
     */
    public static String toJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }

}