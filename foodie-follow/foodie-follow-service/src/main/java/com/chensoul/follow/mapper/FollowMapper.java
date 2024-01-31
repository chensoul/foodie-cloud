package com.chensoul.follow.mapper;

import com.chensoul.follow.entity.Follow;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 关注/取关 Mapper
 */
public interface FollowMapper {

	// 查询关注信息
	@Select("select id, user_id, follow_user_id from t_follow " +
			"where user_id = #{userId} and follow_user_id = #{followUserId}")
	Follow selectFollow(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

	// 添加关注信息
	@Insert("insert into t_follow (user_id, follow_user_id,  create_time, update_time)" +
			" values(#{userId}, #{followUserId},  now(), now())")
	int save(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

	// 修改关注信息
	@Update("update t_follow set update_time = now() where id = #{id}")
	int update(@Param("id") Long id, @Param("isFollowed") int isFollowed);

}
