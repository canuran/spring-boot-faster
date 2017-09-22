package ewing.common;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * GSON工具类。
 */
public final class GsonUtils {

    private static final Gson gson = new GsonBuilder()
            .setLenient()
            .enableComplexMapKeySerialization()
            .serializeNulls()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .disableHtmlEscaping()
            .create();

    private GsonUtils() {
    }

    /**
     * 公开可给外部使用。
     */
    public static Gson getGson() {
        return gson;
    }

    /**
     * 将object对象转成json字符串。
     */
    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    /**
     * 将json字符串转成object。
     */
    public static <T> T toObject(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    /**
     * 将json字符串转成泛型object。
     */
    public static <T> T toObject(String json, TypeToken<T> typeToken) {
        return gson.fromJson(json, typeToken.getType());
    }

    /**
     * 将json字符串转成转成list。
     */
    public static <T> List<T> toList(String arrayJson, Class<T> type) {
        List<T> list = new ArrayList<>();
        JsonArray array = new JsonParser().parse(arrayJson).getAsJsonArray();
        for (JsonElement elem : array) {
            list.add(gson.fromJson(elem, type));
        }
        return list;
    }

}
