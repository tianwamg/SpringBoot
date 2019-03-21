package com.cn.shiro.dao;

import com.cn.shiro.domain.User;
import org.apache.ibatis.annotations.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@Mapper
@CacheConfig(cacheNames = "user")
public interface UserMapper {

    @Insert("insert into user(name,password) values(#{name},#{password})")
    int add(@Param("name") String name, @Param("password") String password);

    @Cacheable(key="#p0")
    @Select("select * from user where id = #{id}")
    User findById(Long id);

    @CachePut(key="#p0")
    @Update("update user set name=#{name},password=#{password} where id=#{id}")
    int updateById(@Param("id") Long id, @Param("name") String name, @Param("password") String password);

    //如果指定为true，则方法调用后立刻清空所有缓存
    @CacheEvict(key = "#p0",allEntries = true)
    @Delete("delete from user where id=#{id}")
    int delete(Long id);

    @Cacheable(key = "#p0")
    @Select("select * from user where username = #{name}")
    User findByName(String name);
}
