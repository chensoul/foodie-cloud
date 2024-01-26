package com.imooc.oauth2.server.mapper;

import com.imooc.commons.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 食客 Mapper
 */
public interface UserMapper {

	@Select("select * from t_diner where " +
			"(username = #{username} or phone = #{username} or email = #{username})")
	User getByUsername(@Param("username") String username);

}
