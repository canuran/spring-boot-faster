package ewing.application.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp请求工具类，支持链式添加多种类型的参数并调用，简化OkHttp的调用流程。
 * 支持多种参数源，大多数情况都不需要手动重组参数，直接从你的上下文给参数即可。
 * <p>
 * OKHttp原始API简化调用，可返回多种结果或回调:
 * OkHttpUtils.callSuccess()/callForResponse()/callForString()
 * /callForBytes()/callForStream()/callForObject()/callback()
 * <p>
 * 使用Url参数的GET请求：
 * OkHttpUtils.get().header().param().map().bean().callXxx()
 * <p>
 * 使用Url参数的DELETE请求：
 * OkHttpUtils.delete().header().param().map().bean().callXxx()
 * <p>
 * 使用Form参数的POST请求：
 * OkHttpUtils.formPost().header().param().map().bean().callXxx()
 * <p>
 * 带文件上传的Form参数的POST请求：
 * OkHttpUtils.multiPost().header().param().file().part().map().bean().callXxx()
 * <p>
 * 使用JSON的Body参数的POST请求：
 * OkHttpUtils.bodyPost().header().json().add().param().map().bean().callXxx()
 * <p>
 * 使用JSON的Body参数的PUT请求：
 * OkHttpUtils.bodyPut().header().json().add().param().map().bean().callXxx()
 *
 * @author Ewing
 * @date 2017/6/15
 */
public class OkHttpUtils {

    public static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES).build();

    public static final MediaType STREAM = MediaType.parse("application/octet-stream");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 私有化构造方法，禁止创建实例。
     */
    private OkHttpUtils() {
    }

    /**
     * 请求器。
     */
    private abstract static class Requester<R extends Requester> {
        protected Request.Builder builder = new Request.Builder();

        public R header(String name, String value) {
            builder.header(name, value);
            return (R) this;
        }

        public abstract R param(String name, Object value);

        public R bean(Object bean) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor descriptor : descriptors) {
                    // 需要可用的属性
                    Method readMethod = descriptor.getReadMethod();
                    if (readMethod == null || descriptor.getWriteMethod() == null) {
                        continue;
                    }
                    Object value = readMethod.invoke(bean);
                    param(descriptor.getName(), value);
                }
            } catch (IntrospectionException | ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            return (R) this;
        }

        public R map(Map<String, ?> map) {
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                param(entry.getKey(), entry.getValue());
            }
            return (R) this;
        }

        protected abstract Request buildRequest();

        public void callSuccess() {
            OkHttpUtils.callSuccess(buildRequest());
        }

        public Response callForResponse() {
            return OkHttpUtils.callForResponse(buildRequest());
        }

        public <T> T callForObject(Class<T> type) {
            return OkHttpUtils.callForObject(buildRequest(), type);
        }

        public <T> T callForObject(TypeToken<T> token) {
            return OkHttpUtils.callForObject(buildRequest(), token);
        }

        public String callForString() {
            return OkHttpUtils.callForString(buildRequest());
        }

        public byte[] callForBytes() {
            return OkHttpUtils.callForBytes(buildRequest());
        }

        public InputStream callForStream() {
            return OkHttpUtils.callForStream(buildRequest());
        }

        public void callback(Callback callback) {
            OkHttpUtils.callback(buildRequest(), callback);
        }
    }

    /**
     * Get请求器。
     */
    public static class Getter extends Requester<Getter> {
        protected StringBuilder urlBuilder;
        private boolean hasParam;

        public Getter(String url) {
            this.urlBuilder = new StringBuilder(url);
            this.hasParam = url.contains("?");
        }

        @Override
        public Getter param(String name, Object value) {
            if (hasParam) {
                urlBuilder.append('&');
            } else {
                urlBuilder.append('?');
                hasParam = true;
            }
            urlBuilder.append(encodeUrl(name)).append('=').append(encodeUrl(value));
            return this;
        }

        public <C extends Collection<?>> Getter collection(String name, C params) {
            StringBuilder builder = new StringBuilder();
            if (params != null && params.size() > 0) {
                for (Object param : params) {
                    if (builder.length() > 0) {
                        builder.append(',');
                    }
                    builder.append(param);
                }
            }
            return param(name, builder.toString());
        }

        public Getter array(String name, Object... params) {
            StringBuilder builder = new StringBuilder();
            if (params != null && params.length > 0) {
                for (Object param : params) {
                    if (builder.length() > 0) {
                        builder.append(',');
                    }
                    builder.append(param);
                }
            }
            return param(name, builder.toString());
        }

        @Override
        protected Request buildRequest() {
            return builder.get().url(urlBuilder.toString()).build();
        }
    }

    /**
     * Post请求器。
     */
    public static class FormPoster extends Requester<FormPoster> {
        protected FormBody.Builder formBuilder = new FormBody.Builder();

        public FormPoster(String url) {
            this.builder.url(url);
        }

        @Override
        public FormPoster param(String name, Object value) {
            formBuilder.add(String.valueOf(name), String.valueOf(value));
            return this;
        }

        @Override
        protected Request buildRequest() {
            return builder.post(formBuilder.build()).build();
        }
    }

    /**
     * 带文件流Post请求器。
     */
    public static class MultiPoster extends Requester<MultiPoster> {
        protected MultipartBody.Builder multiBuilder = new MultipartBody
                .Builder().setType(MultipartBody.FORM);

        public MultiPoster(String url) {
            this.builder.url(url);
        }

        @Override
        public MultiPoster param(String name, Object value) {
            multiBuilder.addFormDataPart(String.valueOf(name), String.valueOf(value));
            return this;
        }

        public MultiPoster file(String name, File file) {
            multiBuilder.addFormDataPart(String.valueOf(name),
                    file.getName(), RequestBody.create(STREAM, file));
            return this;
        }

        public MultiPoster part(MultipartBody.Part part) {
            multiBuilder.addPart(part);
            return this;
        }

        @Override
        protected Request buildRequest() {
            return builder.post(multiBuilder.build()).build();
        }
    }

    /**
     * Body的Post请求器。
     */
    public static class BodyPoster extends Requester<BodyPoster> {
        protected JsonElement jsonBody = JsonNull.INSTANCE;

        public BodyPoster(String url) {
            this.builder.url(url);
        }

        public BodyPoster json(String json) {
            this.jsonBody = GsonUtils.getGson().fromJson(json, JsonElement.class);
            return this;
        }

        public BodyPoster gson(JsonElement json) {
            this.jsonBody = json == null ? JsonNull.INSTANCE : json;
            return this;
        }

        @Override
        public BodyPoster param(String name, Object value) {
            if (jsonBody.isJsonNull()) {
                jsonBody = new JsonObject();
            } else if (!jsonBody.isJsonObject()) {
                throw new RuntimeException("Only JsonObject can add param.");
            }
            String nameStr = String.valueOf(name);
            JsonObject jsonObject = (JsonObject) jsonBody;
            if (value == null) {
                jsonObject.add(nameStr, JsonNull.INSTANCE);
            } else if (value instanceof Number) {
                jsonObject.addProperty(nameStr, (Number) value);
            } else if (value instanceof Boolean) {
                jsonObject.addProperty(nameStr, (Boolean) value);
            } else if (value instanceof Character) {
                jsonObject.addProperty(nameStr, (Character) value);
            } else if (value instanceof String) {
                jsonObject.addProperty(nameStr, (String) value);
            } else {
                jsonObject.add(nameStr, GsonUtils.getGson().toJsonTree(value));
            }
            return this;
        }

        public BodyPoster add(Object value) {
            if (jsonBody.isJsonNull()) {
                jsonBody = new JsonArray();
            } else if (!jsonBody.isJsonArray()) {
                throw new RuntimeException("Only JsonArray can add element.");
            }
            JsonArray jsonArray = (JsonArray) jsonBody;
            if (value == null) {
                jsonArray.add(JsonNull.INSTANCE);
            } else if (value instanceof Number) {
                jsonArray.add((Number) value);
            } else if (value instanceof Boolean) {
                jsonArray.add((Boolean) value);
            } else if (value instanceof Character) {
                jsonArray.add((Character) value);
            } else if (value instanceof String) {
                jsonArray.add((String) value);
            } else {
                jsonArray.add(GsonUtils.getGson().toJsonTree(value));
            }
            return this;
        }

        @Override
        protected Request buildRequest() {
            return builder.post(RequestBody.create(JSON, jsonBody.toString())).build();
        }
    }

    /**
     * Body的Put请求器。
     */
    public static class BodyPutter extends BodyPoster {

        public BodyPutter(String url) {
            super(url);
        }

        @Override
        protected Request buildRequest() {
            return builder.put(RequestBody.create(JSON, jsonBody.toString())).build();
        }
    }

    /**
     * Delete请求器。
     */
    public static class DeleteBuilder extends Getter {

        public DeleteBuilder(String url) {
            super(url);
        }

        @Override
        protected Request buildRequest() {
            return builder.delete().url(urlBuilder.toString()).build();
        }
    }

    /**
     * 准备创建Url的Get请求。
     */
    public static Getter get(String url) {
        return new Getter(url);
    }

    /**
     * 准备创建表单的Post请求。
     */
    public static FormPoster formPost(String url) {
        return new FormPoster(url);
    }

    /**
     * 准备创建带文件的Post请求。
     */
    public static MultiPoster multiPost(String url) {
        return new MultiPoster(url);
    }

    /**
     * 准备创建Body的Post请求。
     */
    public static BodyPoster bodyPost(String url) {
        return new BodyPoster(url);
    }

    /**
     * 准备创建Body的Put请求。
     */
    public static BodyPutter bodyPut(String url) {
        return new BodyPutter(url);
    }

    /**
     * 准备创建Url的Delete请求。
     */
    public static DeleteBuilder delete(String url) {
        return new DeleteBuilder(url);
    }

    /**
     * 使用UTF-8进行URL参数编码。
     */
    public static String encodeUrl(Object source) {
        try {
            return URLEncoder.encode(String.valueOf(source), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行请求要求成功并忽略结果。
     */
    public static void callSuccess(Request request) {
        try {
            Response response = CLIENT.newCall(request).execute();
            response.close(); // 内部静默关闭
            if (!response.isSuccessful()) {
                throw new RuntimeException("Request Failure, Code: "
                        + response.code() + " Message: " + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException("Request IOException.", e);
        }
    }

    /**
     * 执行Request请求并返回Response。
     */
    public static Response callForResponse(Request request) {
        try {
            return CLIENT.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("Request IOException.", e);
        }
    }

    /**
     * 执行Request请求并返回String值。
     */
    public static String callForString(Request request) {
        try {
            Response response = CLIENT.newCall(request).execute();
            String result = response.body().string();
            response.close(); // 内部静默关闭
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Request IOException.", e);
        }
    }

    /**
     * 执行Request请求并返回字节数组。
     */
    public static byte[] callForBytes(Request request) {
        try {
            Response response = CLIENT.newCall(request).execute();
            byte[] result = response.body().bytes();
            response.close(); // 内部静默关闭
            return result;
        } catch (IOException e) {
            throw new RuntimeException("Request IOException.", e);
        }
    }

    /**
     * 执行Request请求并返回数据流。
     */
    public static InputStream callForStream(Request request) {
        return callForResponse(request).body().byteStream();
    }

    /**
     * 执行Request请求并将返回的Json转成对象。
     */
    public static <T> T callForObject(Request request, Class<T> type) {
        String result = OkHttpUtils.callForString(request);
        return GsonUtils.toObject(result, type);
    }

    /**
     * 执行Request请求并将返回的Json转成对象。
     */
    public static <T> T callForObject(Request request, TypeToken<T> token) {
        String result = OkHttpUtils.callForString(request);
        return GsonUtils.toObject(result, token);
    }

    /**
     * 执行Request请求并在完成时执行回调方法。
     */
    public static void callback(Request request, Callback callback) {
        CLIENT.newCall(request).enqueue(callback);
    }

}
