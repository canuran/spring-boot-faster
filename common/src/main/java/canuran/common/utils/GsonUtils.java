package canuran.common.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * GSON工具类。
 *
 * @author canuran
 */
public final class GsonUtils {

    public static final JsonObject EMPTY_JSON_OBJECT = new JsonObject();
    public static final JsonArray EMPTY_JSON_ARRAY = new JsonArray();

    private static final Gson GSON = new GsonBuilder()
            .setLenient()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .disableHtmlEscaping()
            .create();

    private GsonUtils() {
        throw new IllegalStateException("Can not construct GsonUtils");
    }

    /**
     * 公开可给外部使用。
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * 将object对象转成json字符串。
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    /**
     * 将object对象转成json对象。
     */
    public static JsonElement toJsonElement(Object object) {
        return GSON.toJsonTree(object);
    }

    /**
     * 将json字符串转成object。
     */
    public static <T> T toObject(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    /**
     * 静默将json字符串转成object。
     */
    public static <T> T toObjectSilence(String json, Class<T> type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 静默将json字符串转成object。
     */
    public static <T> T toObjectSilence(String json, TypeToken<T> typeToken) {
        try {
            return GSON.fromJson(json, typeToken.getType());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将json字符串转成任意泛型object。
     */
    public static <T> T toObject(String json, TypeToken<T> typeToken) {
        return GSON.fromJson(json, typeToken.getType());
    }

    /**
     * 将json字符串转成object。
     */
    public static <T> T toObject(JsonElement json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    /**
     * 静默将json字符串转成object。
     */
    public static <T> T toObjectSilence(JsonElement json, Class<T> type) {
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 静默将json字符串转成object。
     */
    public static <T> T toObjectSilence(JsonElement json, TypeToken<T> typeToken) {
        try {
            return GSON.fromJson(json, typeToken.getType());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将json字符串转成任意泛型object。
     */
    public static <T> T toObject(JsonElement json, TypeToken<T> typeToken) {
        return GSON.fromJson(json, typeToken.getType());
    }

    /**
     * 通过json对象进行深度拷贝。
     */
    public static <T> T deepCopy(Object source, Class<T> type) {
        if (source == null || type == null) {
            return null;
        }
        return GSON.fromJson(GSON.toJsonTree(source), type);
    }

    /**
     * 通过json对象进行深度拷贝。
     */
    public static <T> T deepCopy(Object source, TypeToken<T> typeToken) {
        if (source == null || typeToken == null) {
            return null;
        }
        return GSON.fromJson(GSON.toJsonTree(source), typeToken.getType());
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
            return GSON.toJson(source);
        }
    }

}
