package com.imooc.diner.mapper;

import com.imooc.commons.model.dto.UserAddRequest;
import com.imooc.commons.model.entity.User;
import com.imooc.commons.model.vo.ShortDinerInfo;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 食客 Mapper
 */
public interface UserMapper {

	// 根据手机号查询食客信息
	@Select("select * from t_diner where phone = #{phone}")
	User getByPhone(@Param("phone") String phone);

	// 根据用户名查询食客信息
	@Select("select * from t_diner where username = #{username}")
	User getByUsername(@Param("username") String username);

	// 新增食客信息
	@Insert("insert into " +
			" t_diner (username, password, phone, roles,  create_time, update_time) " +
			" values (#{username}, #{password}, #{phone}, \"USER\", 1, now(), now())")
	int save(UserAddRequest userAddRequest);

	@Select("<script> " +
			" select id,nickname,avatar from t_diner " +
			" where id in " +
			" <foreach item=\"id\" collection=\"ids\" open=\"(\" separator=\",\" close=\")\"> " +
			"   #{id} " +
			" </foreach> " +
			" </script>")
	List<ShortDinerInfo> findByIds(@Param("ids") String[] ids);

}
