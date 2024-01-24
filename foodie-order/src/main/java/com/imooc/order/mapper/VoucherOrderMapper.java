package com.imooc.order.mapper;

import com.imooc.commons.model.entity.VoucherOrder;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 代金券订单 Mapper
 */
public interface VoucherOrderMapper {

	@Select("select * from t_voucher_order where diner_id = #{dinerId} " +
			" and voucher_id = #{voucherId} and status between 0 and 1 ")
	VoucherOrder findDinerOrder(@Param("dinerId") Long dinerId,
								@Param("voucherId") Long voucherId);

	@Insert("insert into t_voucher_order (order_no, voucher_id, diner_id, " +
			" status, seckill_id, order_type, create_time, update_time)" +
			" values (#{orderNo}, #{voucherId}, #{dinerId}, #{status}, #{seckillId}, " +
			" #{orderType}, now(), now())")
	int save(VoucherOrder voucherOrder);

}
