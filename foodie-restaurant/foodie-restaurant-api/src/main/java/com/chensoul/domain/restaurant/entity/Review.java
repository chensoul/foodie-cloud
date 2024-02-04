package com.chensoul.domain.restaurant.entity;

import com.chensoul.core.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Review extends BaseEntity {
	private static final long serialVersionUID = -4974178162326082799L;
	private Long restaurantId;

	private String content;
	private Long userId;
	private int likeIt;

}
