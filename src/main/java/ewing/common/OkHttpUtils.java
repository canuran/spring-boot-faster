package ewing.common;

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
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp请求工具类，使用链式调用风格，简化OkHttp的调用流程。
 * OkHttpUtils.call...()：OKHttp原始API简化调用，可直接返回对象。
 * OkHttpUtils.get()：使用Url参数的GET请求。
 * OkHttpUtils.formPost()：使用Form参数的POST请求。
 * OkHttpUtils.multiPost()：带文件上传的Form参数的POST请求。
 * OkHttpUtils.bodyPost()：使用JSON的Body参数的POST请求。
 *
 * @author Ewing
 * @date 2017/6/15
 */
public class OkHttpUtils {

    public static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(10, TimeUnit.MINUTES).build();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 私有化构造方法，禁止创建实例。
     */
    private OkHttpUtils() {
    }

    /**
     * 请求构造器。
     */
    private abstract static class RequestBuilder {
        protected Request.Builder builder = new Request.Builder();

        public RequestBuilder header(String name, String value) {
            builder.header(encodeUrl(name), encodeUrl(value));
            return this;
        }

        public abstract RequestBuilder param(String name, Object value);

        public RequestBuilder bean(Object bean) {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
                PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
                for (PropertyDescriptor descriptor : descriptors) {
                    // 需要可用的属性
                    Method readMethod = descriptor.getReadMethod();
                    if (readMethod == null || descriptor.getWriteMethod() == null)
                        continue;
                    Object value = readMethod.invoke(bean);
                    if (value != null)
                        param(descriptor.getName(), value);
                }
            } catch (IntrospectionException | ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            return this;
        }

        public RequestBuilder map(Map<String, Object> map) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                param(entry.getKey(), String.valueOf(entry.getValue()));
            }
            return this;
        }

        protected abstract void beforeCall();

        public Response call() {
            beforeCall();
            return OkHttpUtils.callRequest(builder.build());
        }

        public <T> T callJsonObject(Class<T> type) {
            beforeCall();
            return OkHttpUtils.callJsonObject(builder.build(), type);
        }

        public <T> T callJsonObject(TypeToken<T> token) {
            beforeCall();
            return OkHttpUtils.callJsonObject(builder.build(), token);
        }

        public String callForString() {
            beforeCall();
            return OkHttpUtils.callForString(builder.build());
        }

        public InputStream callForStream() {
            beforeCall();
            return OkHttpUtils.callForStream(builder.build());
        }
    }

    /**
     * Get请求构造器。
     */
    private static class GetBuilder extends RequestBuilder {
        private StringBuilder urlBuilder;
        private boolean hasParam;

        public GetBuilder(String url) {
            this.builder.get();
            this.urlBuilder = new StringBuilder(url);
            this.hasParam = url.contains("?");
        }

        public GetBuilder header(String name, String value) {
            super.header(name, value);
            return this;
        }

        public GetBuilder bean(Object bean) {
            super.bean(bean);
            return this;
        }

        public GetBuilder map(Map<String, Object> map) {
            super.map(map);
            return this;
        }

        public GetBuilder param(String name, Object value) {
            if (hasParam) {
                urlBuilder.append('&');
            } else {
                urlBuilder.append('?');
                hasParam = true;
            }
            urlBuilder.append(encodeUrl(name)).append('=').append(encodeUrl(value));
            return this;
        }

        protected void beforeCall() {
            builder.url(urlBuilder.toString());
        }
    }

    /**
     * Post请求构造器。
     */
    private static class FormPostBuilder extends RequestBuilder {
        private FormBody.Builder formBuilder = new FormBody.Builder();

        public FormPostBuilder(String url) {
            this.builder.url(url);
        }

        public FormPostBuilder header(String name, String value) {
            super.header(name, value);
            return this;
        }

        public FormPostBuilder bean(Object bean) {
            super.bean(bean);
            return this;
        }

        public FormPostBuilder map(Map<String, Object> map) {
            super.map(map);
            return this;
        }

        public FormPostBuilder param(String name, Object value) {
            formBuilder.add(String.valueOf(name), String.valueOf(value));
            return this;
        }

        protected void beforeCall() {
            this.builder.post(formBuilder.build());
        }
    }

    /**
     * 带文件流Post请求构造器。
     */
    private static class MultiFormBuilder extends RequestBuilder {
        private MultipartBody.Builder multiBuilder = new MultipartBody.Builder();

        public MultiFormBuilder(String url) {
            this.builder.url(url);
        }

        public MultiFormBuilder header(String name, String value) {
            super.header(name, value);
            return this;
        }

        public MultiFormBuilder bean(Object bean) {
            super.bean(bean);
            return this;
        }

        public MultiFormBuilder map(Map<String, Object> map) {
            super.map(map);
            return this;
        }

        public MultiFormBuilder param(String name, Object value) {
            multiBuilder.addFormDataPart(String.valueOf(name), String.valueOf(value));
            return this;
        }

        public MultiFormBuilder file(String name, File file) {
            multiBuilder.addFormDataPart(String.valueOf(name),
                    file.getName(), RequestBody.create(null, file));
            return this;
        }

        public MultiFormBuilder part(MultipartBody.Part part) {
            multiBuilder.addPart(part);
            return this;
        }

        protected void beforeCall() {
            this.builder.post(multiBuilder.build());
        }
    }

    /**
     * Body的Post请求构造器。
     */
    private static class BodyPostBuilder extends RequestBuilder {
        private JsonElement jsonElement = new JsonObject();

        public BodyPostBuilder(String url) {
            this.builder.url(url);
        }

        public BodyPostBuilder header(String name, String value) {
            super.header(name, value);
            return this;
        }

        public BodyPostBuilder bean(Object bean) {
            super.bean(bean);
            return this;
        }

        public BodyPostBuilder json(String json) {
            this.jsonElement = GsonUtils.getGson().toJsonTree(json);
            return this;
        }

        public BodyPostBuilder map(Map<String, Object> map) {
            super.map(map);
            return this;
        }

        public BodyPostBuilder param(String name, Object value) {
            if (!jsonElement.isJsonObject())
                throw new RuntimeException("Only JsonObject can add param.");
            String nameStr = String.valueOf(name);
            JsonObject jsonObject = (JsonObject) jsonElement;
            if (value == null) {
                jsonObject.add(nameStr, JsonNull.INSTANCE);
            } else if (value instanceof Number) {
                jsonObject.addProperty(nameStr, (Number) value);
            } else if (value instanceof Boolean) {
                jsonObject.addProperty(nameStr, (Boolean) value);
            } else if (value instanceof Character) {
                jsonObject.addProperty(nameStr, (Character) value);
            } else {
                jsonObject.addProperty(nameStr, String.valueOf(value));
            }
            return this;
        }

        public BodyPostBuilder add(Object value) {
            if (!jsonElement.isJsonArray())
                throw new RuntimeException("Only JsonArray can add element.");
            JsonArray jsonArray = (JsonArray) jsonElement;
            if (value == null) {
                jsonArray.add(JsonNull.INSTANCE);
            } else if (value instanceof Number) {
                jsonArray.add((Number) value);
            } else if (value instanceof Boolean) {
                jsonArray.add((Boolean) value);
            } else if (value instanceof Character) {
                jsonArray.add((Character) value);
            } else {
                jsonArray.add(String.valueOf(value));
            }
            return this;
        }

        protected void beforeCall() {
            this.builder.post(RequestBody.create(JSON, jsonElement.toString()));
        }
    }

    /**
     * 准备创建Url的Get请求。
     */
    public static GetBuilder get(String url) {
        return new GetBuilder(url);
    }

    /**
     * 准备创建表单的Post请求。
     */
    public static FormPostBuilder formPost(String url) {
        return new FormPostBuilder(url);
    }

    /**
     * 准备创建带文件的Post请求。
     */
    public static MultiFormBuilder multiPost(String url) {
        return new MultiFormBuilder(url);
    }

    /**
     * 准备创建Body的Post请求。
     */
    public static BodyPostBuilder bodyPost(String url) {
        return new BodyPostBuilder(url);
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
     * 执行Request请求。
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
     */
    public static InputStream callForStream(Request request) {
        return callRequest(request).body().byteStream();
    }

    /**
     * 执行Request请求并将返回的Json转成对象。
     */
    public static <T> T callJsonObject(Request request, Class<T> type) {
        String result = OkHttpUtils.callForString(request);
        return GsonUtils.toObject(result, type);
    }

    /**
     * 执行Request请求并将返回的Json转成对象。
     */
    public static <T> T callJsonObject(Request request, TypeToken<T> token) {
        String result = OkHttpUtils.callForString(request);
        return GsonUtils.toObject(result, token);
    }

}
