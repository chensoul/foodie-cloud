package com.chensoul.foodie.domain.order.entity;

import com.chensoul.core.model.BaseEntity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SeckillVoucher extends BaseEntity {

	private static final long serialVersionUID = -8368221217142979462L;
	private Long voucherId;
	private int amount;


	private LocalDateTime startTime;


	private LocalDateTime endTime;

}
