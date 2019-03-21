package com.cn.shiro.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * redis配置类
 */
@Configuration
//使配置生效
@EnableCaching
public class RedisConfiguration extends CachingConfigurerSupport {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfiguration.class);

    @Resource
    private LettuceConnectionFactory lettuceConnectionFactory;

    private Duration duration =Duration.ofSeconds(60);

    @Override
    @Bean
    public KeyGenerator keyGenerator(){
        //设置自动key的生成规则,配置springboot的注解,进行方法级别的缓存
        //使用:进行分割，可以显示出层级关系
        return (target,method,params) ->{
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(target.getClass().getName());
            stringBuilder.append(":");
            stringBuilder.append(method.getName());
            for(Object obj:params){
                stringBuilder.append(":"+String.valueOf(obj));
            }
            String rsToUse = String.valueOf(stringBuilder);
            logger.info("自动生成Redis Key ->[{}]",rsToUse);
            return rsToUse;
        };
    }

    @Override
    @Bean
    public CacheManager cacheManager(){
        //初始化缓存管理器
        logger.info("初始化 ->[{}]","CacheManager RedisCacheManager start");
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))//设置key序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))//value序列化
                .disableCachingNullValues()
                .entryTtl(duration);//设置缓存过期时间
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(lettuceConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .transactionAware();
        return builder.build();
    }

    /**
     * RedisTemplate配置，在单独使用redisTemplate时，重新定义序列化方式
     * @return
     */
    /*@Bean
    public RedisTemplate<String,Object> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory){
        //设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL,JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        //配置redisTemplate
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        RedisSerializer<?> serializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }*/

    private RedisSerializer<String> keySerializer(){
        return new StringRedisSerializer();
    }

    private RedisSerializer<Object> valueSerializer(){
        //设置序列化
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL,JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(mapper);
        return jackson2JsonRedisSerializer;
        //或者使用
        //return new GenericJackson2JsonRedisSerializer();
    }
}
