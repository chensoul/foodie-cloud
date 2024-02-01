package com.chensoul.foodie.domain.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chensoul.foodie.domain.user.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 食客 Mapper
 */
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where " +
            "(username = #{username} or phone = #{username} or email = #{username})")
    User loadUserByUsername(@Param("username") String username);
}
