package com.chensoul.domain.point.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chensoul.domain.point.entity.Point;
import com.chensoul.domain.point.model.UserPointRankVO;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 积分 Mapper
 */
public interface PointMapper extends BaseMapper<Point> {
	// 查询积分排行榜 TOPN
	@Select("SELECT t1.user_id AS id, " +
			" sum( t1.score ) AS total, " +
			" rank () over ( ORDER BY sum( t1.score ) DESC ) AS rank," +
			" t2.nickname, t2.avatar " +
			" FROM foodie t1 LEFT JOIN user t2 ON t1.user_id = t2.id " +
			" GROUP BY t1.user_id " +
			" ORDER BY total DESC LIMIT #{top}")
	List<UserPointRankVO> findTopN(@Param("top") int top);

	// 根据食客 ID 查询当前食客的积分排名
	@Select("SELECT id, total, ranks, nickname, avatar FROM (" +
			" SELECT t1.user_id AS id, " +
			" sum( t1.score ) AS total, " +
			" rank () over ( ORDER BY sum( t1.score ) DESC ) AS rank," +
			" t2.nickname, t2.avatar " +
			" FROM foodie t1 LEFT JOIN user t2 ON t1.user_id = t2.id " +
			" GROUP BY t1.user_id " +
			" ORDER BY total DESC ) r " +
			" WHERE id = #{userId}")
	UserPointRankVO findUserRank(@Param("userId") Long userId);

}
