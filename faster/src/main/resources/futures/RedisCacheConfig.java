package futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis缓存配置。
 */
/*
1、引入Redis依赖
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
2、application.yml
spring:
  redis: # Redis基本配置
    host: localhost
    port: 6379
    database: 0
    timeout: 10000
    pool:
      maxIdle: 20
      maxActive: 100
*/
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Autowired
    private RedisConnectionFactory factory;

    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
        // 默认缓存一天 86400秒
        redisCacheManager.setDefaultExpiration(86400L);
        return redisCacheManager;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        // 字符串Key序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 对象值序列化
        ObjectRedisSerializer objectRedisSerializer = new ObjectRedisSerializer();
        redisTemplate.setValueSerializer(objectRedisSerializer);
        redisTemplate.setHashValueSerializer(objectRedisSerializer);
        return redisTemplate;
    }

}