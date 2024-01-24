package com.imooc.follow.mapper;

import com.imooc.commons.model.entity.Follow;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 关注/取关 Mapper
 */
public interface FollowMapper {

	// 查询关注信息
	@Select("select id, diner_id, follow_diner_id from t_follow " +
			"where diner_id = #{dinerId} and follow_diner_id = #{followDinerId}")
	Follow selectFollow(@Param("dinerId") Long dinerId, @Param("followDinerId") Long followDinerId);

	// 添加关注信息
	@Insert("insert into t_follow (diner_id, follow_diner_id,  create_time, update_time)" +
			" values(#{dinerId}, #{followDinerId},  now(), now())")
	int save(@Param("dinerId") Long dinerId, @Param("followDinerId") Long followDinerId);

	// 修改关注信息
	@Update("update t_follow set update_time = now() where id = #{id}")
	int update(@Param("id") Long id, @Param("isFollowed") int isFollowed);

}
