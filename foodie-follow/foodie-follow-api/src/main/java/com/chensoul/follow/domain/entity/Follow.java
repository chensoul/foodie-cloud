package com.chensoul.follow.domain.entity;

import com.chensoul.commons.model.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Follow extends BaseEntity {
	private static final long serialVersionUID = 2840550359254845479L;
	private Long userId;
	private Long followUserId;

}
