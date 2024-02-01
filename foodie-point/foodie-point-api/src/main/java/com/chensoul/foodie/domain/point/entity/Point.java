package com.chensoul.foodie.domain.point.entity;

import com.chensoul.core.model.BaseEntity;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Point extends BaseEntity {
	private static final long serialVersionUID = -3051862591232648062L;

	@NotNull(message = "用户id不能为空")
	private Long userId;

	@NotNull(message = "积分不能为空")
	private Long score;

	/**
	 * 类型 0=签到，1=关注好友，2=添加Feed，3=添加商户评论
	 */
	@NotNull(message = "积分类型不能为空")
	private Integer type;

}
