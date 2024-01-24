package com.imooc.feed.mapper;

import com.imooc.commons.model.entity.Feed;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface FeedMapper {

	// 添加 Feed
	@Insert("insert into t_feed (content, diner_id, praise_amount, " +
			" comment_amount, restaurant_id, create_time, update_time) " +
			" values (#{content}, #{fkDinerId}, #{praiseAmount}, #{commentAmount}, #{fkRestaurantId}, " +
			" now(), now())")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int save(Feed feed);

	// 查询 Feed
	@Select("select id, content, diner_id, praise_amount, " +
			" comment_amount, restaurant_id, create_time, update_time " +
			" from t_feed where id = #{id}")
	Feed findById(@Param("id") Long id);

	// 逻辑删除 Feed
	@Update("delete from t_feed where id = #{id} ")
	int delete(@Param("id") Long id);

	// 根据食客 ID 查询 Feed
	@Select("select id, content, update_time from t_feed " +
			" where diner_id = #{dinerId}")
	List<Feed> findByDinerId(@Param("dinerId") Long dinerId);

	// 根据多主键查询 Feed
	@Select("<script> " +
			" select id, content, diner_id, praise_amount, " +
			" comment_amount, restaurant_id, create_time, update_time" +
			" from t_feed where id in " +
			" <foreach item=\"id\" collection=\"feedIds\" open=\"(\" separator=\",\" close=\")\">" +
			"   #{id}" +
			" </foreach> order by id desc" +
			" </script>")
	List<Feed> findByIds(@Param("feedIds") Set<Long> feedIds);

}
