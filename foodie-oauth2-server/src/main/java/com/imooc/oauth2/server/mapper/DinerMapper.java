package com.imooc.oauth2.server.mapper;

import com.imooc.commons.model.entity.Diner;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 食客 Mapper
 */
public interface DinerMapper {

	@Select("select * from t_diner where " +
			"(username = #{account} or phone = #{account} or email = #{account})")
	Diner getByAccount(@Param("account") String account);

}
