package ewing.common;

import okhttp3.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * OkHttp请求工具类。
 *
 * @author Ewing
 * @date 2017/6/15
 */
public class OkHttpUtils {

    public static final OkHttpClient CLIENT = new OkHttpClient();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 私有化构造方法，禁止创建实例。
     */
    private OkHttpUtils() {
    }

    /**
     * 使用UTF-8进行URL参数编码。
     */
    public static String encodeUrl(String source) {
        try {
            return URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行Request请求。
     *
     * @param request Request请求。
     * @return Response对象。
     */
    public static Response callRequest(Request request) {
        try {
            return CLIENT.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("Request IOException.", e);
        }
    }

    /**
     * 执行Request请求并返回String值。
     *
     * @param request Request请求。
     * @return Body中的String。
     */
    public static String callForString(Request request) {
        try {
            Response response = CLIENT.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException("Request IOException.", e);
        }
    }

    /**
     * 执行Request请求并返回数据流。
     *
     * @param request Request请求。
     * @return Body中的数据流。
     */
    public static InputStream callForStream(Request request) {
        return callRequest(request).body().byteStream();
    }

    /**
     * 提交JSON并返回String。
     *
     * @param url  请求地址。
     * @param json Json数据。
     * @return 返回内容。
     */
    public static String postJson(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        return callForString(request);
    }

    /**
     * 提交普通Form并返回String。
     *
     * @param url       请求地址。
     * @param keyValues 表单键值对：key1,values1,key2,values2等。
     * @return 返回内容。
     */
    public static String postForm(String url, String... keyValues) {
        int max = (keyValues.length >> 1) << 1;
        FormBody.Builder builder = new FormBody.Builder();
        for (int i = 0; i < max; i++)
            builder.add(keyValues[i++], keyValues[i]);
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        return callForString(request);
    }

    /**
     * 提交普通Form并返回String。
     *
     * @param url    请求地址。
     * @param params 表单参数。
     * @return 返回内容。
     */
    public static String postForm(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet())
            builder.add(entry.getKey(), entry.getValue());
        RequestBody body = builder.build();
        Request request = new Request.Builder().url(url).post(body).build();
        return callForString(request);
    }

    /**
     * 发起Get请求并返回String。
     *
     * @param url       请求地址。
     * @param keyValues 表单键值对：key1,values1,key2,values2等。
     * @return 返回内容。
     */
    public static String getByParam(String url, String... keyValues) {
        int max = (keyValues.length >> 1) << 1;
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append(urlBuilder.indexOf("?") == -1 ? '?' : '&');
        for (int i = 0; i < max; i++) {
            if (i > 0) urlBuilder.append('&');
            urlBuilder.append(keyValues[i++]).append('=').append(encodeUrl(keyValues[i]));
        }
        Request request = new Request.Builder().url(urlBuilder.toString()).get().build();
        return callForString(request);
    }

}
