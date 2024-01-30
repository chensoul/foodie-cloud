package com.imooc.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.auth.entity.User;
import com.imooc.auth.model.dto.UserAddRequest;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 食客 Mapper
 */
public interface UserMapper extends BaseMapper<User> {

	@Select("select * from t_user where " +
			"(username = #{username} or phone = #{username} or email = #{username})")
	User loadUserByUsername(@Param("username") String username);

	// 根据手机号查询食客信息
	@Select("select * from t_user where phone = #{phone}")
	User getByPhone(@Param("phone") String phone);

	// 根据用户名查询食客信息
	@Select("select * from t_user where username = #{username}")
	User getByUsername(@Param("username") String username);

	// 新增食客信息
	@Insert("insert into " +
			" t_user (username, password, phone, roles,  create_time, update_time) " +
			" values (#{username}, #{password}, #{phone}, \"USER\", 1, now(), now())")
	int save(UserAddRequest userAddRequest);


}
