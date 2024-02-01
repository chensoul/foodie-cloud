package com.chensoul.foodie.domain.order.mapper;

import com.chensoul.foodie.domain.order.entity.SeckillVoucher;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 秒杀代金券 Mapper
 */
public interface SeckillVoucherMapper {

	// 新增秒杀活动
	@Insert("insert into seckill_voucher (voucher_id, amount, start_time, end_time, create_time, update_time) " +
			" values (#{voucherId}, #{amount}, #{startTime}, #{endTime}, now(), now())")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int save(SeckillVoucher seckillVoucher);

	// 根据代金券 ID 查询该代金券是否参与抢购活动
	@Select("select id, voucher_id, amount, start_time, end_time, is_valid " +
			" from seckill_voucher where voucher_id = #{voucherId}")
	SeckillVoucher selectVoucher(Long voucherId);

	// 减库存
	@Update("update seckill_voucher set amount = amount - 1 " +
			" where id = #{seckillId}")
	int stockDecrease(@Param("seckillId") Long seckillId);

}
