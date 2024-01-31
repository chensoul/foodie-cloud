package com.chensoul.order.mapper;

import com.chensoul.commons.model.entity.VoucherOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 代金券订单 Mapper
 */
public interface VoucherOrderMapper {

	@Select("select * from t_voucher_order where user_id = #{userId} " +
			" and voucher_id = #{voucherId} and status between 0 and 1 ")
	VoucherOrder findUserOrder(@Param("userId") Long userId,
							   @Param("voucherId") Long voucherId);

	@Insert("insert into t_voucher_order (order_no, voucher_id, user_id, " +
			" status, seckill_id, order_type, create_time, update_time)" +
			" values (#{orderNo}, #{voucherId}, #{userId}, #{status}, #{seckillId}, " +
			" #{orderType}, now(), now())")
	int save(VoucherOrder voucherOrder);

}
