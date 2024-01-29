package com.imooc.point.mapper;

import com.imooc.commons.model.entity.UserPoint;
import com.imooc.commons.model.vo.UserPointRankVO;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 积分 Mapper
 */
public interface PointMapper {

	// 添加积分
	@Insert("insert into t_point (user_id, point, type,create_time, update_time) " +
			" values (#{fkUserId}, #{point}, #{type},now(), now())")
	void save(UserPoint userPoint);

	// 查询积分排行榜 TOPN
	@Select("SELECT t1.user_id AS id, " +
			" sum( t1.point ) AS total, " +
			" rank () over ( ORDER BY sum( t1.point ) DESC ) AS ranks," +
			" t2.nickname, t2.avatar " +
			" FROM t_point t1 LEFT JOIN t_users t2 ON t1.user_id = t2.id " +
			" GROUP BY t1.user_id " +
			" ORDER BY total DESC LIMIT #{top}")
	List<UserPointRankVO> findTopN(@Param("top") int top);

	// 根据食客 ID 查询当前食客的积分排名
	@Select("SELECT id, total, ranks, nickname, avatar FROM (" +
			" SELECT t1.user_id AS id, " +
			" sum( t1.point ) AS total, " +
			" rank () over ( ORDER BY sum( t1.point ) DESC ) AS ranks," +
			" t2.nickname, t2.avatar " +
			" FROM t_point t1 LEFT JOIN t_users t2 ON t1.user_id = t2.id " +
			" GROUP BY t1.user_id " +
			" ORDER BY total DESC ) r " +
			" WHERE id = #{userId}")
	UserPointRankVO findUserRank(@Param("userId") Long userId);

}
