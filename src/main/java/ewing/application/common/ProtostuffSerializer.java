package ewing.application.common;

import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Protostuff序列化，线程安全，支持任意对象类型。
 * 使用对象图，支持循环引用、集合类属性等复杂对象。
 *
 * @author Ewing
 * @date 2017/6/15
 */
public class ProtostuffSerializer {

    private static Schema<Content> schema = RuntimeSchema.getSchema(Content.class);

    /**
     * 私有化构造方法。
     */
    private ProtostuffSerializer() {
    }

    /**
     * 内部用于包装原始对象的类。
     */
    private static class Content {
        private Object object;

        Content(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }
    }

    /**
     * 对象序列化。
     *
     * @param object 对象实例。
     * @return 序列化字节。
     */
    public static byte[] serialize(Object object) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return GraphIOUtil.toByteArray(new Content(object), schema, buffer);
    }

    /**
     * 对象反序列化。
     *
     * @param bytes 序列化字节。
     * @return 对象实例。
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        Content content = schema.newMessage();
        GraphIOUtil.mergeFrom(bytes, content, schema);
        return (T) content.getObject();
    }

}
