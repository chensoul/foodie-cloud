package com.imooc.point.mapper;

import com.imooc.commons.model.entity.DinerPoint;
import com.imooc.commons.model.vo.DinerPointRankVO;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 积分 Mapper
 */
public interface DinerPointMapper {

	// 添加积分
	@Insert("insert into t_diner_point (diner_id, point, type,create_time, update_time) " +
			" values (#{fkDinerId}, #{point}, #{type},now(), now())")
	void save(DinerPoint dinerPoint);

	// 查询积分排行榜 TOPN
	@Select("SELECT t1.diner_id AS id, " +
			" sum( t1.point ) AS total, " +
			" rank () over ( ORDER BY sum( t1.point ) DESC ) AS ranks," +
			" t2.nickname, t2.avatar_url " +
			" FROM t_diner_point t1 LEFT JOIN t_diners t2 ON t1.diner_id = t2.id " +
			" GROUP BY t1.diner_id " +
			" ORDER BY total DESC LIMIT #{top}")
	List<DinerPointRankVO> findTopN(@Param("top") int top);

	// 根据食客 ID 查询当前食客的积分排名
	@Select("SELECT id, total, ranks, nickname, avatar_url FROM (" +
			" SELECT t1.diner_id AS id, " +
			" sum( t1.point ) AS total, " +
			" rank () over ( ORDER BY sum( t1.point ) DESC ) AS ranks," +
			" t2.nickname, t2.avatar_url " +
			" FROM t_diner_point t1 LEFT JOIN t_diners t2 ON t1.diner_id = t2.id " +
			" GROUP BY t1.diner_id " +
			" ORDER BY total DESC ) r " +
			" WHERE id = #{dinerId}")
	DinerPointRankVO findDinerRank(@Param("dinerId") Long dinerId);

}
