package com.chensoul.foodie.domain.entity;

import com.chensoul.core.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Follow extends BaseEntity {
	private static final long serialVersionUID = 2840550359254845479L;
	private Long userId;
	private Long followUserId;

}
