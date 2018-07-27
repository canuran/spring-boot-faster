package futures;

import ewing.common.ProtostuffSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Redis对象序列化。
 */
public class ObjectRedisSerializer implements RedisSerializer {

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        return ProtostuffSerializer.serialize(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return ProtostuffSerializer.deserialize(bytes);
    }

}
